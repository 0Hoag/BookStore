package com.example.identityservice.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.identityservice.constant.PredefinedRole;
import com.example.identityservice.dto.request.*;
import com.example.identityservice.dto.request.response.AuthenticationResponse;
import com.example.identityservice.dto.request.response.GithubUserResponse;
import com.example.identityservice.dto.request.response.IntrospectResponse;
import com.example.identityservice.entity.InvalidatedToken;
import com.example.identityservice.entity.Role;
import com.example.identityservice.entity.User;
import com.example.identityservice.entity.UserImage;
import com.example.identityservice.enums.ImageType;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.repository.InvalidatedRepository;
import com.example.identityservice.repository.UserImageRepository;
import com.example.identityservice.repository.UserRepository;
import com.example.identityservice.repository.httpclient.*;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    UserImageRepository userImageRepository;
    InvalidatedRepository invalidatedRepository;
    OutboundGoogleIdentityClient outboundGoogleIdentityClient;
    OutboundGithubIdentityClient outboundGithubIdentityClient;
    OutboundGoogleUserClient outboundUserClient;
    OutboundGithubUserClient outboundGithubUserClient;
    ProfileClient profileClient;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long valid_duration;

    @NonFinal
    @Value("${jwt.refreshtable-duration}")
    protected long refreshtable_duration;

    @NonFinal
    @Value("${outbound.google.client-id}")
    protected String GOOGLE_CLIENT_ID;

    @NonFinal
    @Value("${outbound.google.client-secret}")
    protected String GOOGLE_CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.google.redirect-uri}")
    protected String GOOGLE_REDIRECT_URI;

    @NonFinal
    @Value("${outbound.github.client-id}")
    protected String GITHUB_CLIENT_ID;

    @NonFinal
    @Value("${outbound.github.client-secret}")
    protected String GITHUB_CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.github.redirect-uri}")
    protected String GITHUB_REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    public IntrospectResponse introspectResponse(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public GithubUserResponse getUserInfo(String accessToken) {
        return outboundGithubUserClient.getUser("Bearer " + accessToken);
    }

    public AuthenticationResponse outboundAuthenticationGithub(String code) {
        var response = outboundGithubIdentityClient.exchangeToken(ExchangeGithubTokenRequest.builder()
                .clientId(GITHUB_CLIENT_ID)
                .clientSecret(GITHUB_CLIENT_SECRET)
                .code(code)
                .build());

        log.info("Token response: {}", response);

        // get user info
        var userInfo = getUserInfo(response.getAccessToken());
        log.info("UserInfo: {}", userInfo);

        Optional<User> existedUser = userRepository.findByUsername(userInfo.getLogin());

        if (existedUser.isPresent()) {
            var token = generateToken(existedUser.get());
            log.info("token: {}", token);
            return AuthenticationResponse.builder().token(token).build();
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder().name(PredefinedRole.USER_ROLE).build());

        var user = userRepository.save(User.builder()
                .username(userInfo.getLogin())
                .firstName(userInfo.getName())
                .lastName(userInfo.getName())
                .email(userInfo.getEmail())
                .roles(roles)
                .build());

        Set<UserImage> images = new HashSet<>();
        UserImage image = UserImage.builder()
                .imageUrl(userInfo.getAvatar_url())
                .user(user)
                .imageType(ImageType.PROFILE)
                .build();

        userImageRepository.save(image);
        images.add(image);
        user.setImages(images);
        userRepository.save(user);

        ProfileCreationRequest request = ProfileCreationRequest.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(LocalDate.now())
                .city("null")
                .build();

        profileClient.createProfile(request);

        // convert token github => token database (identity)
        var token = generateToken(user);
        log.info("Token {}", token);

        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse outboundAuthenticationGoogle(String code) {
        var response = outboundGoogleIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(GOOGLE_CLIENT_ID)
                .clientSecret(GOOGLE_CLIENT_SECRET)
                .redirectUri(GOOGLE_REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

        log.info("TOKEN RESPONSE {}", response);

        // Get user info
        var userInfo = outboundUserClient.getUser("json", response.getAccessToken());
        log.info("UserInfo {}", userInfo);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder().name(PredefinedRole.USER_ROLE).build());

        // Onboard user
        // create User and set info user google and save database(identity)
        var user = userRepository
                .findByUsername(userInfo.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(userInfo.getEmail())
                        .username(userInfo.getEmail())
                        .firstName(userInfo.getGivenName())
                        .lastName(userInfo.getFamilyName())
                        .roles(roles)
                        .build()));

        ProfileCreationRequest request = ProfileCreationRequest.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(LocalDate.now())
                .city("null")
                .build();

        profileClient.createProfile(request);

        // convert token google => token database (identity)
        var token = generateToken(user);
        log.info("Token {}", token);

        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse authenticated(AuthenticationRequest request) {
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.USER_NOT_EXISTED);

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).exprityTime(expiryTime).build();

            invalidatedRepository.save(invalidatedToken);
        } catch (AppException e) {
            log.info("Token already expired");
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        // - check Authenticated
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var exprityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).exprityTime(exprityTime).build();

        invalidatedRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserId())
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(valid_duration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(refreshtable_duration, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }
}

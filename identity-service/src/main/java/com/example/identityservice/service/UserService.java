package com.example.identityservice.service;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.identityservice.enums.ImageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.event.dto.*;
import com.example.identityservice.constant.PredefinedRole;
import com.example.identityservice.dto.request.*;
import com.example.identityservice.dto.request.response.*;
import com.example.identityservice.dto.request.response.OrdersResponse.OrdersResponse;
import com.example.identityservice.entity.*;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapper.*;
import com.example.identityservice.repository.*;
import com.example.identityservice.repository.httpclient.BookClient;
import com.example.identityservice.repository.httpclient.ProfileClient;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepositories;
    RoleRepositoty roleRepository;
    CartItemRepository cartItemRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    ProfileMapper profileMapper;
    ProfileClient profileClient;
    BookClient bookClient;
    CartItemMapper cartItemMapper;
    SelectedProductMapper selectedProductMapper;
    SelectedProductRepository selectedProductRepository;
    OrdersRepository ordersRepository;
    OrdersMapper ordersMapper;
    KafkaTemplate<String, Object> kafkaTemplate;
    UserImageRepository userImageRepository;
    UserImageService userImageService;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long valid_duration;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    @NonFinal
    @Value("${cloudinary.cloud-name}")
    protected String cloudName;

    @NonFinal
    @Value("${cloudinary.api-key}")
    protected String apiKey;

    @NonFinal
    @Value("${cloudinary.api-secret}")
    protected String apiSecret;

    public UserResponse createUser(UserCreationRequest request) {

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        try {
            user = userRepositories.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXITSTED);
        }

        var profileRequest = profileMapper.toProfileCreationRequest(request);
        profileRequest.setUserId(user.getUserId());

        profileClient.createProfile(profileRequest);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(request.getEmail())
                .subject("Welcome to HoagTeria")
                .body("Hello " + request.getUsername())
                .build();

        // public message to kafka
        //        kafkaTemplate.send("notification-delivery", notificationEvent);

        return userMapper.toUserResponse(user);
    }

    public void createPassword(String userId, PasswordCreationRequest request) {
        User user = userRepositories.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (StringUtils.hasText(user.getPassword())) throw new AppException(ErrorCode.PASSWORD_EXISTED);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepositories.save(user);
    }

    public void changePassword(PasswordChangeRequest request) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        User user = userRepositories.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepositories.save(user);
    }

    public void verifyPassword(PasswordVerifyRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepositories
                .findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
    }

    @PreAuthorize("hasAuthority('GET_DATA')")
    public UserResponse getUser(String userId) {
        User user = userRepositories.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.getCartItem().size();
        //        user.getSelectedProducts().size();

        UserResponse userResponse = userMapper.toUserResponse(user);

        Set<CartItemResponse> cartItemResponses = selectedCartItemResponse(userResponse.getCartItem());

        Set<OrdersResponse> ordersResponses = selectedOrdersResponse(userResponse.getOrders());

        userResponse.setCartItem(cartItemResponses);
        userResponse.setOrders(ordersResponses);

        return userResponse;
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        User user = userRepositories.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setNoPassword(!StringUtils.hasText(user.getPassword()));

        Set<CartItemResponse> cartItemResponses = selectedCartItemResponse(userResponse.getCartItem());

        Set<OrdersResponse> ordersResponses = selectedOrdersResponse(userResponse.getOrders());

        userResponse.setCartItem(cartItemResponses);
        userResponse.setOrders(ordersResponses);

        return userResponse;
    }

    @PreAuthorize("hasAuthority('GET_DATA')")
    public List<UserResponse> getAllUser() {
        return userRepositories.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public List<UserInformationBasicResponse> getAllUserInformationBasic() {
        List<User> userInformationBasicResponses =
                userRepositories.findAll().stream().toList();
        List<UserInformationBasicResponse> informationBasicResponse =
                convertToUserInformationBasicResponses(userInformationBasicResponses);
        return informationBasicResponse;
    }

    public UserInformationBasicResponse getUserInformationBasic(String userId) {
        var user = userRepositories.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return UserInformationBasicResponse.builder()
                .username(user.getUsername())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .userId(user.getUserId())
                .build();
    }

    public List<UserInformationBasicResponse> convertToUserInformationBasicResponses(List<User> entity) {
        return entity.stream().map(this::convertToUserInformationResponse).collect(Collectors.toList());
    }

    public UserInformationBasicResponse convertToUserInformationResponse(User entity) {
        return new UserInformationBasicResponse(
                entity.getUserId(),
                entity.getUsername(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getImages());
    }

    public Set<CartItemResponse> selectedCartItemResponse(Set<CartItemResponse> cartItemResponses) {
        Set<CartItemResponse> cartItemResponses1 = cartItemResponses.stream()
                .map(cartItemResponse -> {
                    CartItem cartItem = cartItemRepository
                            .findById(cartItemResponse.getCartItemId())
                            .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_EXISTED));
                    CartItemResponse itemResponse = cartItemMapper.toCartItemResponse(cartItem);
                    BookResponse bookResponse =
                            fetchBookResponse(itemResponse.getBookId().getBookId(), generateToken(cartItem.getUser()));
                    itemResponse.setBookId(bookResponse);
                    return itemResponse;
                })
                .collect(Collectors.toSet());
        return cartItemResponses1;
    }

    public Set<OrdersResponse> selectedOrdersResponse(Set<OrdersResponse> ordersResponse) {
        Set<OrdersResponse> ordersResponses = ordersResponse.stream()
                .map(ordersResponses1 -> {
                    Orders orders = ordersRepository
                            .findById(ordersResponses1.getOrderId())
                            .orElseThrow(() -> new AppException(ErrorCode.ORDERS_NOT_EXISTED));
                    OrdersResponse ordersResponse1 = ordersMapper.toOrdersResponse(orders);
                    Set<SelectedProductResponse> selectedProductResponses = orders.getSelectedProducts().stream()
                            .map(selectedProductResponse -> {
                                SelectedProduct selectedProduct = selectedProductRepository
                                        .findById(selectedProductResponse.getSelectedId())
                                        .orElseThrow(() -> new AppException(ErrorCode.SELECTED_PRODUCT_NOT_EXISTED));
                                SelectedProductResponse selectedProductResponse1 =
                                        selectedProductMapper.toSelectedProductResponse(selectedProduct);
                                BookResponse bookResponse = fetchBookResponse(
                                        selectedProduct.getBookId(), generateToken(selectedProduct.getUser()));
                                selectedProductResponse1.setBookId(bookResponse);
                                return selectedProductResponse1;
                            })
                            .collect(Collectors.toSet());
                    ordersResponse1.setSelectedProducts(selectedProductResponses);
                    return ordersResponse1;
                })
                .collect(Collectors.toSet());
        return ordersResponses;
    }

    public UserResponse updateInformationUser(UpdateInformationRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepositories.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateInformationUser(user, request);
        return userMapper.toUserResponse(userRepositories.save(user));
    }

    public Map<String, Object> uploadPhoto(MultipartFile file) throws IOException {
        String fileName = FileUtils.generateFileName("File", FileUtils.getExtension(file.getName()));
        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);

        try {
            if (FileUtils.validateFile(file)) {
                file.transferTo(tempFile);

                Map<String, Object> uploadResult = cloudinaryConfig().uploader().upload(tempFile, ObjectUtils.emptyMap());
                return uploadResult;
            }else {
                throw new AppException(ErrorCode.UPLOAD_FILE_FAIL);
            }
        }catch (IOException e) {
            throw new AppException(ErrorCode.UPLOAD_FILE_FAIL);
        }finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    public void uploadImageUserProfile(String userId, MultipartFile file) throws IOException, SQLException {
        var user = userRepositories.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Map<String, Object> uploadResult = uploadPhoto(file);
        String imageUrl = (String) uploadResult.get("url");

        UserImage image = userImageService.toCreateUserImage(UserImageRequest.builder()
                        .imageType(ImageType.PROFILE)
                        .imageUrl(imageUrl)
                        .user(userId)
                .build());

        user.getImages().add(image);
        userRepositories.save(user);
    }

    public void uploadImageUserCover(String userId, MultipartFile file) throws IOException {
        var user = userRepositories.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Map<String, Object> uploadResult = uploadPhoto(file);
        String imageUrl = (String) uploadResult.get("url");

        UserImage image = userImageService.toCreateUserImage(UserImageRequest.builder()
                        .imageUrl(imageUrl)
                        .imageType(ImageType.COVER)
                        .user(userId)
                .build());

        user.getImages().add(image);
        userRepositories.save(user);
    }

    public void removeImageUser(String userId, RemoveUserImage image) {
        var user = userRepositories.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
        Set<UserImage> userImages = userImageRepository.findAllById(image.getImages())
                .stream().collect(Collectors.toSet());

        for (UserImage image1: userImages) {
            try {
                String publicId = extractPublicIdFromUrl(image1.getImageUrl());
                log.info("publicId: {}", publicId);

                Map result = cloudinaryConfig().uploader().destroy(publicId, ObjectUtils.emptyMap());

                if ("ok".equals(result.get("result"))) {
                    log.info("Successfully delete image from Cloudinary: {}", publicId);
                    user.getImages().remove(image1);
                }else {
                    log.error("Failed to delete image from Cloudinary: {}", publicId);
                    throw new AppException(ErrorCode.REMOVE_FILE_FAIL);
                }
            }catch (IOException e) {
                throw new AppException(ErrorCode.REMOVE_FILE_FAIL);
            }
        }
        userRepositories.save(user);
    }

    public String extractPublicIdFromUrl(String imageUrl) {
        String[] urlParts = imageUrl.split("/");
        int uploadIndex = -1;
        for (int i = 0;i < urlParts.length;i++) {
            if ("upload".equals(urlParts[i])) {
                uploadIndex = i;
                break;
            }
        }

        if (uploadIndex == -1 || uploadIndex == urlParts.length - 1) {
            throw new IllegalArgumentException("Invalid Cloudinary URL format");
        }

        return String.join("/", Arrays.stream(urlParts, uploadIndex + 1, urlParts.length)
                        .filter(part -> !part.startsWith("v"))
                        .collect(Collectors.toList()))
                .replaceFirst("[.][^.]+$", "");
    }


    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        log.info("Service: updateUser");
        User user = userRepositories.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_EXITSTED));

        userMapper.updateUser(user, request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepositories.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepositories.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userRepositories.deleteById(userId);
        profileClient.deleteProfileUserId(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllUser() {
        userRepositories.deleteAll();
        profileClient.deleteAllProfile();
    }

    public void userExisted(String userId) {
        userRepositories.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public BookResponse fetchBookResponse(String bookId, String token) {
        ApiResponse<BookResponse> apiResponse = bookClient.getBook(bookId, "Bearer " + token);
        return apiResponse.getResult();
    }

    @Bean
    public Cloudinary cloudinaryConfig() {
        Cloudinary cloudinary = null;
        Map config = new HashMap();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        cloudinary = new Cloudinary(config);
        return cloudinary;
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
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

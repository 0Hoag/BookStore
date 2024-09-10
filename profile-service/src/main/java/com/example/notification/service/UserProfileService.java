package com.example.notification.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.notification.dto.request.AuthenticationRequest;
import com.example.notification.dto.response.*;

import com.example.notification.repository.httpClient.IdentityClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.notification.dto.request.ProfileCreationRequest;
import com.example.notification.dto.request.ProfileUpdateRequest;
import com.example.notification.entity.UserProfile;
import com.example.notification.exception.AppException;
import com.example.notification.exception.ErrorCode;
import com.example.notification.mapper.UserProfileMapper;
import com.example.notification.repository.UserProfileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;
    IdentityClient identityClient;


    public UserProfile createProfile(ProfileCreationRequest request) {
        // take role form ADMIN
//        String token = getTokenFromIdentityService();
//        log.info("Token {}", token);

        // create profile (entity)
//        UserProfile userProfile = userProfileMapper.toUserProfile(request);
//        userProfile = userProfileRepository.save(userProfile);
//        String userId = userProfile.getUserId();
//        log.info("UserProfile {}", userProfile);

//        UserResponse userResponse = fetchUserInformation(userId, token);
//        log.info("UserResponse {}", userResponse);

        // create Role and set Role to roleResponses
//        Set<RoleResponse> roleResponses = new HashSet<>();
//        for (RoleResponse role : userResponse.getRoles()) {
//            // take roles
//            roleResponses.add(role);
//        }
//
//        UserProfileResponse userProfileResponse = userProfileMapper.toUserProfileResponse(userProfile);
//        userResponse.setRoles(new HashSet<>(roleResponses));
//        userProfileResponse.setUser(userResponse);
//        log.info("UserProfileResponse {}", userProfileResponse);
//
//        return userProfileResponse;

        var userProfile = userProfileMapper.toUserProfile(request);
        userProfileRepository.save(userProfile);
        return userProfile;
    }

    public UserProfileResponse getProfile(String profileId) {
        UserProfile userProfile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));
        String token = getTokenFromIdentityService();

        UserResponse userResponse = fetchUserInformationBasic(userProfile.getUserId(), token);

        UserProfileResponse response = userProfileMapper.toUserProfileResponse(userProfile);
        response.setUser(userResponse);

        return response;
    }

    public ProfileExistedResponse profileExists(String userId) {
        boolean exists = userProfileRepository.existsByUserId(userId);
        return new ProfileExistedResponse(userId, exists);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserProfileResponse> getAllProfile() {
        String token = getTokenFromIdentityService();

        return userProfileRepository.findAll().stream()
                .map(userProfile -> {
                    UserResponse userResponse = fetchUserInformationBasic(userProfile.getUserId(), token);

                    UserProfileResponse response = userProfileMapper.toUserProfileResponse(userProfile);
                    response.setUser(userResponse);

                    return response;
                }).toList();
    }

    //bug(need fix now)
    public UserProfileResponse getProfileByUserId(String userId) {
        String token = getTokenFromIdentityService();

        UserResponse userResponse = fetchUserInformationBasic(userId, token);

        UserProfileResponse response = userProfileMapper.toUserResponse(userResponse);
        response.setUser(userResponse);

        return response;
    }

    //bug(need fix now)
//    public UserProfileResponse getMyInfo() {
//        String token = getTokenFromIdentityService();
//        String userId = getUserIdFromToken(token);
//
//        UserResponse userResponse = fetchUserInformation(userId, token);
//
//        UserProfile profile = userProfileRepository.findByUserId(userId)
//                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_EXISTED));
//
//        Set<RoleResponse> roleResponses = userResponse.getRoles();
//        userResponse.setRoles(roleResponses);
//
//        UserProfileResponse response = userProfileMapper.toUserProfileResponse(profile);
//        response.setUser(userResponse);
//
//        return response;
//    }


    private String getTokenFromIdentityService() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("admin", "admin");
        ApiResponse<AuthenticationResponse> authResponse = identityClient.getToken(authenticationRequest);
        return authResponse.getResult().getToken();
    }

    private UserResponse fetchUserInformationBasic(String userId, String token) {
        ApiResponse<UserResponse> userResponseApiResponse = identityClient.getUserInfomationBasic(userId, "Bearer " + token);
        return userResponseApiResponse.getResult();
    }


    public void deleteProfile(String profileId) {
        UserProfile userProfile = userProfileRepository
                .findById(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));
        userProfileRepository.deleteById(profileId);
    }

    public UserProfileResponse updateProfile(String profileId, ProfileUpdateRequest request) {
        UserProfile userProfile = userProfileRepository
                .findById(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));
        userProfileMapper.updateProfile(userProfile, request);
        return userProfileMapper.toUserProfileResponse(userProfileRepository.save(userProfile));
    }

    public void deleteProfileByUserId(String userId) {
        userProfileRepository.deleteByUserId(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllProfile() {
        List<UserProfile> userProfiles =
                userProfileRepository.findAll().stream().toList();
        userProfileRepository.deleteAll(userProfiles);
    }
}

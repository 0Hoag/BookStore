package com.example.notification.mapper;

import com.example.notification.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.notification.dto.request.ProfileCreationRequest;
import com.example.notification.dto.request.ProfileUpdateRequest;
import com.example.notification.dto.response.UserProfileResponse;
import com.example.notification.entity.UserProfile;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    UserProfileResponse toUserProfileResponse(UserProfile entity);

    UserProfileResponse toUserResponse(UserResponse entity);

    void updateProfile(@MappingTarget UserProfile userProfile, ProfileUpdateRequest request);

    default UserResponse map(String userId) {
        if (userId == null) {
            return null;
        }
        return UserResponse.builder().userId(userId).build();
    }
}
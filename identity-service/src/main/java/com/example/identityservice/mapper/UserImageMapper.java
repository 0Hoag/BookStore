package com.example.identityservice.mapper;

import com.example.identityservice.dto.request.UserImageRequest;
import com.example.identityservice.dto.request.UserImageUpdateRequest;
import com.example.identityservice.dto.request.response.UserImageResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.entity.UserImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserImageMapper {
    UserImage toUserImage(UserImageRequest request);

    UserImageResponse toUserImageResponse(UserImage entity);

    void updateUserImage(@MappingTarget UserImage entity, UserImageUpdateRequest request);

    default User mapUserId(String userId) {
        return User.builder().userId(userId).build();
    }

    default String mapUser(User user) {
        return user.getUserId();
    }
}

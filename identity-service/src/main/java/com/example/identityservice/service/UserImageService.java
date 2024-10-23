package com.example.identityservice.service;

import com.example.identityservice.dto.request.UserImageRequest;
import com.example.identityservice.dto.request.UserImageUpdateRequest;
import com.example.identityservice.dto.request.response.UserImageResponse;
import com.example.identityservice.entity.UserImage;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapper.UserImageMapper;
import com.example.identityservice.repository.UserImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserImageService {
    UserImageRepository userImageRepository;
    UserImageMapper userImageMapper;

    public UserImage toCreateUserImage(UserImageRequest request) {
        var userImage = userImageMapper.toUserImage(request);
        return userImageRepository.save(userImage);
    }

    public UserImageResponse getUserImage(String imageId) {
        var image =  userImageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
        return userImageMapper.toUserImageResponse(image);
    }

    public List<UserImage> getAll() {
        return userImageRepository.findAll();
    }

    public void deleteUserImage(String imageId) {
        userImageRepository.deleteById(imageId);
    }

    public UserImageResponse updateUserImage(String imageId, UserImageUpdateRequest request) {
        var userImage = userImageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
        userImageMapper.updateUserImage(userImage, request);
        return userImageMapper.toUserImageResponse(userImageRepository.save(userImage));
    }
}

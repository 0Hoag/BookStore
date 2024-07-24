package com.example.post_service.mapper;


import com.example.post_service.dto.request.CreatePostRequest;
import com.example.post_service.dto.response.PostResponse;
import com.example.post_service.dto.response.UserResponse;
import com.example.post_service.entity.PostStatusBook;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostStatusBook toPostStatusBook(CreatePostRequest request);

    PostResponse toPostResponse(PostStatusBook entity);

//    void updateProfile(@MappingTarget BookProfile bookProfile, UpdateBookRequest request);
    default UserResponse map(String userId) {
        if (userId == null) {
            return null;
        }
        return UserResponse.builder().id(userId).build();
    }
}

package com.example.post_service.mapper;

import com.example.post_service.dto.request.CreatePostRequest;
import com.example.post_service.dto.request.UpdatePostRequest;
import com.example.post_service.dto.response.CommentResponse;
import com.example.post_service.dto.response.LikeResponse;
import com.example.post_service.dto.response.PostResponse;
import com.example.post_service.dto.response.UserResponse;
import com.example.post_service.entity.Post;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostMapper {
//    @Mapping(source = "images", target = "images", qualifiedByName = "mapMultipartFileToString")
//    @Mapping(source = "videos", target = "videos", qualifiedByName = "mapMultipartFileToString")
    Post toPostStatusBook(CreatePostRequest request);

    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "mapLocalDateTimeToInstant")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "mapLocalDateTimeToInstant")
    PostResponse toPostResponse(Post entity);

    @Named("mapUserIdToUserResponseInPost")
    default UserResponse mapUserIdToUserResponseInPost(String userId) {
        if (userId == null) {
            return null;
        }
        return UserResponse.builder().userId(userId).build();
    }

    void updatePost(@MappingTarget Post post, UpdatePostRequest request);

    @Named("mapLocalDateTimeToInstant")
    default Instant mapLocalDateTimeToInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atZone(ZoneId.systemDefault()).toInstant() : null;
    }

    default Set<LikeResponse> mapLikeResponse(Set<String> strings) {
        return strings.stream()
                .map(s -> LikeResponse.builder().likeId(s).build())
                .collect(Collectors.toSet());
    }

    default Set<CommentResponse> mapCommentResponse(Set<String> strings) {
        return strings.stream()
                .map(s -> CommentResponse.builder().commentId(s).build())
                .collect(Collectors.toSet());
    }

//    @Named("mapMultipartFileToString")
//    default Set<String> mapMultipartFileToString(Set<MultipartFile> files) {
//        if (files == null) {
//            return null;
//        }
//        return files.stream()
//                .map(MultipartFile::getOriginalFilename)
//                .collect(Collectors.toSet());
//    }
}
package com.example.post_service.mapper;


import com.example.post_service.dto.request.CreatePostRequest;
import com.example.post_service.dto.request.UpdatePostRequest;
import com.example.post_service.dto.response.CommentResponse;
import com.example.post_service.dto.response.LikeResponse;
import com.example.post_service.dto.response.PostResponse;
import com.example.post_service.dto.response.UserResponse;
import com.example.post_service.entity.Post;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toPostStatusBook(CreatePostRequest request);
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "mapLocalDateTimeToInstant")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "mapLocalDateTimeToInstant")
    @Mapping(source = "userId", target = "userId", qualifiedByName = "mapUserIdToUserResponseInPost")
    PostResponse toPostResponse(Post entity);

    @Named("mapUserIdToUserResponseInPost")
    default UserResponse mapUserIdToUserResponseInPost(String userId) {
        if (userId == null) {
            return null;
        }
        return UserResponse.builder().userId(userId).build();
    }

    @IterableMapping(qualifiedByName = "mapUserIdToUserResponseInPost")
    Set<UserResponse> mapUserIds(Set<String> userIds);

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
}
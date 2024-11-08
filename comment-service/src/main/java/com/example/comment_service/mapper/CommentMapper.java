package com.example.comment_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.comment_service.dto.request.CreateCommentRequest;
import com.example.comment_service.dto.request.UpdateCommentRequest;
import com.example.comment_service.dto.response.CommentResponse;
import com.example.comment_service.dto.response.UserResponse;
import com.example.comment_service.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toComment(CreateCommentRequest request);

    //    @Mapping(source = "userId", target = "userId", qualifiedByName = "mapUserIdToUserResponseInComment")
    CommentResponse toCommentResponse(Comment entity);

    void updateComment(@MappingTarget Comment comment, UpdateCommentRequest request);

    //    @Named("mapUserIdToUserResponseInComment")
    default UserResponse mapUserIdToUserResponseInComment(String userId) {
        if (userId == null) {
            return null;
        }
        return UserResponse.builder().userId(userId).build();
    }

    //    @IterableMapping(qualifiedByName = "mapUserIdToUserResponseInComment")
    //    Set<UserResponse> mapUserIds(Set<String> userIds);
}

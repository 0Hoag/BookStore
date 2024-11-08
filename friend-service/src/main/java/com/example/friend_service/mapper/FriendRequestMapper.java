package com.example.friend_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import com.example.friend_service.dto.request.CreateFriendRequest;
import com.example.friend_service.dto.request.UpdateFriendStatus;
import com.example.friend_service.dto.response.FriendResponse;
import com.example.friend_service.entity.FriendRequest;

@Mapper(componentModel = "spring")
public interface FriendRequestMapper {
    FriendRequest toFriendRequest(CreateFriendRequest request);

    FriendResponse toFriendResponse(FriendRequest entity);

    @Named("updateFriendStatus")
    default FriendRequest updateFriendStatus(FriendRequest entity, UpdateFriendStatus status) {
        if (entity != null && status != null) {
            entity.setCondition(status.getCondition());
        }
        return entity;
    }
}

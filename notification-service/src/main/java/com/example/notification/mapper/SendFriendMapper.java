package com.example.notification.mapper;

import org.mapstruct.Mapper;

import com.example.notification.dto.request.SendFriendRequest;
import com.example.notification.dto.response.SendFriendResponse;
import com.example.notification.entity.SendFriend;

@Mapper(componentModel = "spring")
public interface SendFriendMapper {
    SendFriend toSendFriend(SendFriendRequest request);

    SendFriendResponse toSendFriendResponse(SendFriend entity);
}

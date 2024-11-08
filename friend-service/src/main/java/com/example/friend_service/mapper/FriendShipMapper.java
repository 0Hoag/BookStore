package com.example.friend_service.mapper;

import org.mapstruct.Mapper;

import com.example.friend_service.dto.request.FriendShipRequest;
import com.example.friend_service.dto.response.FriendShipResponse;
import com.example.friend_service.entity.FriendShip;

@Mapper(componentModel = "spring")
public interface FriendShipMapper {
    FriendShip toFriendShip(FriendShipRequest request);

    FriendShipResponse toFriendResponse(FriendShip friendShip);
}

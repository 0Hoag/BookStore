package com.example.friend_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.friend_service.dto.request.FriendShipRequest;
import com.example.friend_service.dto.response.FriendShipResponse;
import com.example.friend_service.enums.Condition;
import com.example.friend_service.enums.RelationShip;
import com.example.friend_service.exception.AppException;
import com.example.friend_service.exception.ErrorCode;
import com.example.friend_service.mapper.FriendShipMapper;
import com.example.friend_service.repository.FriendRequestRepository;
import com.example.friend_service.repository.FriendShipRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FriendShipService {
    FriendShipRepository friendShipRepository;
    FriendRequestRepository friendRequestRepository;
    FriendShipMapper friendShipMapper;

    public FriendShipResponse createFriendShip(FriendShipRequest request) {
        var friendRequest =
                friendRequestRepository.findBySenderIdAndReceiverId(request.getUserId1(), request.getUserId2());

        if (friendRequest == null) {
            friendRequest =
                    friendRequestRepository.findBySenderIdAndReceiverId(request.getUserId2(), request.getUserId1());
        }

        if (friendRequest == null || friendRequest.getCondition() != Condition.ACCEPTED) {
            throw new AppException(ErrorCode.FRIENDS_SHIP_NOT_VALIDATOR);
        }

        var friendShip = friendShipMapper.toFriendShip(request);
        friendShip.setRelationShip(RelationShip.FRIENDS);
        friendShip.setSince(LocalDateTime.now());

        friendRequestRepository.deleteById(friendRequest.getRequestId()); // when accept remove request

        return friendShipMapper.toFriendResponse(friendShipRepository.save(friendShip));
    }

    public FriendShipResponse getFriendShip(String friendshipId) {
        var friendShip = friendShipRepository
                .findById(friendshipId)
                .orElseThrow(() -> new AppException(ErrorCode.FRIENDS_SHIP_NOT_EXISTED));
        return friendShipMapper.toFriendResponse(friendShip);
    }

    public List<FriendShipResponse> getAll() {
        return friendShipRepository.findAll().stream()
                .map(friendShipMapper::toFriendResponse)
                .toList();
    }

    public void deleteFriendShip(String friendshipId) {
        friendShipRepository
                .findById(friendshipId)
                .orElseThrow(() -> new AppException(ErrorCode.FRIENDS_REQUEST_NOT_EXISTED));
        friendShipRepository.deleteById(friendshipId);
    }

    public void deleteAllFriendShip() {
        friendShipRepository.deleteAll();
    }
}

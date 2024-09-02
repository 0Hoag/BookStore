package com.example.friend_service.service;


import com.example.friend_service.dto.request.AuthenticationRequest;
import com.example.friend_service.dto.response.*;
import com.example.friend_service.entity.BlockList;
import com.example.friend_service.entity.FriendRequest;
import com.example.friend_service.entity.FriendShip;
import com.example.friend_service.mapper.BlockListMapper;
import com.example.friend_service.mapper.FriendRequestMapper;
import com.example.friend_service.mapper.FriendShipMapper;
import com.example.friend_service.repository.BlockListRepository;
import com.example.friend_service.repository.FeignClient.IdentityClient;
import com.example.friend_service.repository.FriendRequestRepository;
import com.example.friend_service.repository.FriendShipRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserRelationshipService {
    FriendRequestRepository friendRequestRepository;
    FriendShipRepository friendShipRepository;
    BlockListRepository blockListRepository;
    IdentityClient identityClient;

    FriendRequestMapper friendRequestMapper;
    FriendShipMapper friendShipMapper;
    BlockListMapper blockListMapper;

    public UserRelationshipInfo getUserRelationShip(String userId) {
        //List entity
        List<FriendRequest> sentRequests = friendRequestRepository.findBySenderId(userId);
        List<FriendRequest> receivedRequests = friendRequestRepository.findByReceiverId(userId);

        List<FriendShip> friendships = friendShipRepository.findByUserId1OrUserId2(userId, userId);

        List<BlockList> blockedUsers = blockListRepository.findByUserId(userId);
        List<BlockList> blockedByUsers = blockListRepository.findByBlockedUserId(userId);

        //convertToResponse
        List<FriendResponse> sentRequest = convertToFriendResponses(sentRequests);
        List<FriendResponse> receivedRequest = convertToFriendResponses(receivedRequests);

        List<FriendShipResponse> friendship = convertToFriendShipResponses(friendships);

        List<BlockListResponse> blockedUser = convertToBlockLists(blockedUsers);
        List<BlockListResponse> blockedByUser = convertToBlockLists(blockedByUsers);

        return new UserRelationshipInfo(sentRequest, receivedRequest, friendship, blockedUser, blockedByUser);
    }

    public List<String> getFriendIds(String userId) {
        List<FriendShip> friendships = friendShipRepository.findByUserId1OrUserId2(userId, userId);
        return friendships.stream()
                .map(fs -> fs.getUserId1().equals(userId) ? fs.getUserId2() : fs.getUserId1())
                .collect(Collectors.toList());
    }

    public List<FriendShip> getAllFriendShip(String userId) {
        return friendRequestRepository.findAllByUserId(userId);
    }

    public String generatedToken() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("admin", "admin");
        ApiResponse<AuthenticationResponse> authResponse = identityClient.getToken(authenticationRequest);
        return authResponse.getResult().getToken();
    }

    //convertToFriendResponse
    public List<FriendResponse> convertToFriendResponses(List<FriendRequest> entity) {
        return entity.stream()
                .map(this::convertToFriendResponse)
                .collect(Collectors.toList());
    }
    public FriendResponse convertToFriendResponse(FriendRequest entity) {
        return friendRequestMapper.toFriendResponse(entity);
    }

    //convertToFriendShipResponse
    public List<FriendShipResponse> convertToFriendShipResponses(List<FriendShip> entity) {
        return entity.stream()
                .map(this::convertToFriendShipResponse)
                .collect(Collectors.toList());
    }
    public FriendShipResponse convertToFriendShipResponse(FriendShip entity) {
        return friendShipMapper.toFriendResponse(entity);
    }

    //convertToBlockLists
    public List<BlockListResponse> convertToBlockLists(List<BlockList> entity) {
        return entity.stream()
                .map(this::convertToBlockList)
                .collect(Collectors.toList());
    }

    public BlockListResponse convertToBlockList(BlockList entity) {
        return blockListMapper.toBlockListResponse(entity);
    }


}

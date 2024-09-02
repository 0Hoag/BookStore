package com.example.friend_service.service;

import com.example.friend_service.dto.request.AuthenticationRequest;
import com.example.friend_service.dto.request.BlockListRequest;
import com.example.friend_service.dto.request.BlockListUpdateRequest;
import com.example.friend_service.dto.response.ApiResponse;
import com.example.friend_service.dto.response.AuthenticationResponse;
import com.example.friend_service.dto.response.BlockListResponse;
import com.example.friend_service.dto.response.UserResponse;
import com.example.friend_service.exception.AppException;
import com.example.friend_service.exception.ErrorCode;
import com.example.friend_service.mapper.BlockListMapper;
import com.example.friend_service.repository.BlockListRepository;
import com.example.friend_service.repository.FeignClient.IdentityClient;
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
public class BlockListService {
    BlockListMapper blockListMapper;
    BlockListRepository blockListRepository;
    IdentityClient identityClient;

    public BlockListResponse createBlockList(BlockListRequest request) {
       var check = blockListRepository.findByBlockedUserId(request.getBlockedUserId());
       var check1 = blockListRepository.findByBlockedUserId(request.getUserId());

       if (check1 == null || check == null) {
           throw new AppException(ErrorCode.BLOCK_LIST_NOT_EXISTED);
       }

       var blockList = blockListMapper.toBlockList(request);

       return blockListMapper.toBlockListResponse(blockListRepository.save(blockList));
    }

    public BlockListResponse getBlockList(String blockId) {
        var blockList = blockListRepository.findById(blockId)
                .orElseThrow(() -> new AppException(ErrorCode.BLOCK_LIST_NOT_EXISTED));

        String token = getTokenFromIdentityService();
        UserResponse userResponse = fetchUserInformation(blockList.getUserId(), token);
        if (userResponse.getUserId().equals(blockList.getUserId())) return blockListMapper.toBlockListResponse(blockList);
        else throw new AppException(ErrorCode.USER_NOT_EXISTED);
    }

    public List<BlockListResponse> getAllBlockList() {
        return blockListRepository.findAll()
                .stream().map(blockListMapper::toBlockListResponse)
                .toList();
    }

    public void deleteBlockList(String blockId) {
        blockListRepository.deleteById(blockId);
    }

    public BlockListResponse updateBlockList(String blockId, BlockListUpdateRequest request) {
        var blockList = blockListRepository.findById(blockId)
                .orElseThrow(() -> new AppException(ErrorCode.BLOCK_LIST_NOT_EXISTED));
        blockListMapper.updateBlockList(blockList, request);
        return blockListMapper.toBlockListResponse(blockListRepository.save(blockList));
    }

    public void deleteAll() {
        blockListRepository.deleteAll();
    }

    private String getTokenFromIdentityService() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("admin", "admin");
        ApiResponse<AuthenticationResponse> authResponse = identityClient.getToken(authenticationRequest);
        return authResponse.getResult().getToken();
    }

    private UserResponse fetchUserInformation(String userId, String token) {
        ApiResponse<UserResponse> userResponseApiResponse = identityClient.getUser(userId, "Bearer " + token);
        return userResponseApiResponse.getResult();
    }
}

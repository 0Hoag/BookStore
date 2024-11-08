package com.example.friend_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.friend_service.dto.request.BlockListRequest;
import com.example.friend_service.dto.request.BlockListUpdateRequest;
import com.example.friend_service.dto.response.BlockListResponse;
import com.example.friend_service.entity.BlockList;

@Mapper(componentModel = "spring")
public interface BlockListMapper {
    BlockList toBlockList(BlockListRequest request);

    BlockListResponse toBlockListResponse(BlockList entity);

    void updateBlockList(@MappingTarget BlockList blockList, BlockListUpdateRequest request);
}

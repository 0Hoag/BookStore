package com.example.friend_service.mapper;

import com.example.friend_service.dto.request.BlockListRequest;
import com.example.friend_service.dto.request.BlockListUpdateRequest;
import com.example.friend_service.dto.response.BlockListResponse;
import com.example.friend_service.entity.BlockList;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BlockListMapper {
    BlockList toBlockList(BlockListRequest request);

    BlockListResponse toBlockListResponse(BlockList entity);

    void updateBlockList(@MappingTarget BlockList blockList, BlockListUpdateRequest request);
}
package com.example.friend_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.friend_service.entity.BlockList;

@Repository
public interface BlockListRepository extends MongoRepository<BlockList, String> {
    List<BlockList> findByUserId(String userId);

    List<BlockList> findByBlockedUserId(String blockedUserId);
}

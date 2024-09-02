package com.example.friend_service.repository;

import com.example.friend_service.entity.BlockList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockListRepository extends MongoRepository<BlockList, String> {
    List<BlockList> findByUserId(String userId);
    List<BlockList> findByBlockedUserId(String blockedUserId);
}

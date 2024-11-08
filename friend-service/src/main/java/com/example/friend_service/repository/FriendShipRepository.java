package com.example.friend_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.friend_service.entity.FriendShip;

@Repository
public interface FriendShipRepository extends MongoRepository<FriendShip, String> {
    List<FriendShip> findByUserId1OrUserId2(String userId, String sameUserId);
}

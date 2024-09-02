package com.example.friend_service.repository;

import com.example.friend_service.entity.FriendRequest;
import com.example.friend_service.entity.FriendShip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendShipRepository extends MongoRepository<FriendShip, String> {
    List<FriendShip> findByUserId1OrUserId2(String userId, String sameUserId);
}

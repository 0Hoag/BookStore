package com.example.friend_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.friend_service.entity.FriendRequest;
import com.example.friend_service.entity.FriendShip;

@Repository
public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {
    FriendRequest findBySenderIdAndReceiverId(String senderId, String receiverId);

    List<FriendRequest> findBySenderId(String senderId);

    List<FriendRequest> findByReceiverId(String receiverId);

    @Query("{ '$or': [ { 'userId1': ?0 }, { 'userId2': ?0 } ] }")
    List<FriendShip> findAllByUserId(String userId);
}

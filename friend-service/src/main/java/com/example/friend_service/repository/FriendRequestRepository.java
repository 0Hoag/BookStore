package com.example.friend_service.repository;

import com.example.friend_service.dto.request.CreateFriendRequest;
import com.example.friend_service.dto.response.FriendResponse;
import com.example.friend_service.entity.FriendRequest;
import com.example.friend_service.entity.FriendShip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {
    FriendRequest findBySenderIdAndReceiverId(String senderId, String receiverId);
    List<FriendRequest> findBySenderId(String senderId);
    List<FriendRequest> findByReceiverId(String receiverId);

    @Query("{ '$or': [ { 'userId1': ?0 }, { 'userId2': ?0 } ] }")
    List<FriendShip> findAllByUserId(String userId);
}

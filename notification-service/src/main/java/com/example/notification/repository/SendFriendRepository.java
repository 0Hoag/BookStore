package com.example.notification.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.notification.entity.SendFriend;

@Repository
public interface SendFriendRepository extends MongoRepository<SendFriend, String> {
    List<SendFriend> findBySenderId(String senderId);

    //    List<SendFriend> findBySendFriendBySenderIdHaveConditionPending(String senderId);
    //
    //    List<SendFriend> findBySendFriendBySenderIdHaveConditionAccepted(String senderId);
    //
    //    List<SendFriend> findBySendFriendBySenderIdHaveConditionBlock(String senderId);

}

package com.example.notification.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.notification.entity.CartNotification;

@Repository
public interface CartNotificationRepository extends MongoRepository<CartNotification, String> {
    List<CartNotification> findByUserId(String userId);

    List<CartNotification> findByUserIdAndIdReadTrueOrderByTimestampDesc(String userId);

    List<CartNotification> findByUserIdAndIdReadFalseOrderByTimestampDesc(String userId);

    List<CartNotification> findByUserIdOrderByTimestampDesc(String userId);

    long countByUserIdAndIdReadFalse(String userId);
}

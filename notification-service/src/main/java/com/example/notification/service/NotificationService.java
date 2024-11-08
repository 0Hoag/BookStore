package com.example.notification.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.notification.dto.request.CartNotificationRequest;
import com.example.notification.dto.response.CartNotificationResponse;
import com.example.notification.entity.CartNotification;
import com.example.notification.mapper.CartNotificationMapper;
import com.example.notification.repository.CartNotificationRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    CartNotificationRepository repository;
    CartNotificationMapper cartNotificationMapper;
    KafkaTemplate<String, CartNotification> kafkaTemplate;

    public CartNotificationResponse createNotification(CartNotificationRequest request) {
        var cartNotification = cartNotificationMapper.toCartNotification(request);
        cartNotification.generateMessage();
        repository.save(cartNotification);

        kafkaTemplate.send("cart-notifications", cartNotification);

        return cartNotificationMapper.toCartNotificationResponse(cartNotification);
    }

    public List<CartNotificationResponse> getNotificationUser(String userId) {
        return repository.findByUserId(userId).stream()
                .map(cartNotificationMapper::toCartNotificationResponse)
                .collect(Collectors.toList());
    }

    public List<CartNotificationResponse> getUserReadNotification(String userId) {
        return repository.findByUserIdAndIdReadTrueOrderByTimestampDesc(userId).stream()
                .map(cartNotificationMapper::toCartNotificationResponse)
                .collect(Collectors.toList());
    }

    public List<CartNotificationResponse> getUserUnReadNotification(String userId) {
        return repository.findByUserIdAndIdReadFalseOrderByTimestampDesc(userId).stream()
                .map(cartNotificationMapper::toCartNotificationResponse)
                .collect(Collectors.toList());
    }

    public void markAsRead(String notificationId) {
        repository.findById(notificationId).ifPresent(notification -> {
            notification.setIdRead(true);
            repository.save(notification);
        });
    }

    public void markAllAsRead(String userId) {
        List<CartNotification> notifications = repository.findByUserIdAndIdReadFalseOrderByTimestampDesc(userId);
        notifications.forEach(cartNotification -> {
            cartNotification.setIdRead(true);
            repository.save(cartNotification);
        });
    }

    public long getUnreadCount(String userId) {
        return repository.countByUserIdAndIdReadFalse(userId);
    }

    public void deleteNotification(String notificationId) {
        repository.deleteById(notificationId);
    }
}

package com.example.notification.mapper;

import org.mapstruct.Mapper;

import com.example.notification.dto.request.CartNotificationRequest;
import com.example.notification.dto.response.CartNotificationResponse;
import com.example.notification.entity.CartNotification;

@Mapper
public interface CartNotificationMapper {
    CartNotification toCartNotification(CartNotificationRequest request);

    CartNotificationResponse toCartNotificationResponse(CartNotification entity);
}

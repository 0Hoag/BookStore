package com.example.notification.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.event.dto.NotificationEvent;
import com.example.notification.dto.request.Recipient;
import com.example.notification.dto.request.SendEmailRequest;
import com.example.notification.dto.response.CartNotificationResponse;
import com.example.notification.dto.response.MessagingResponse;
import com.example.notification.dto.response.SendFriendResponse;
import com.example.notification.entity.CartNotification;
import com.example.notification.entity.SendFriend;
import com.example.notification.repository.CartNotificationRepository;
import com.example.notification.repository.SendFriendRepository;
import com.example.notification.service.EmailService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    EmailService emailService;
    CartNotificationRepository repository;
    SendFriendRepository sendFriendRepository;

    SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "notification-delivery",
            containerFactory = "notificationEventConcurrentKafkaListenerContainerFactory")
    public void listenNotificationDelivery(NotificationEvent event) {
        emailService.sendEmail(SendEmailRequest.builder()
                .to(Recipient.builder().email(event.getRecipient()).build())
                .subject(event.getSubject())
                .htmlContent(event.getBody())
                .build());
        log.info("Message: {}", event);
    }

    @KafkaListener(
            topics = "${spring.kafka.cart-notification.topic}",
            groupId = "${spring.kafka.cart-notification.group-id}",
            containerFactory = "cartKafkaListenerContainerFactory")
    public void consumeCartNotification(CartNotification notification) {
        log.info("Received notification for userId: {} - {}", notification.getUserId(), notification.getMessage());

        try {
            repository.save(notification);

            messagingTemplate.convertAndSend(
                    "/topic/messages/" + notification.getUserId(),
                    CartNotificationResponse.builder()
                            .notificationId(notification.getNotificationId())
                            .userId(notification.getUserId())
                            .message(notification.getMessage())
                            .idRead(false)
                            .timestamp(notification.getTimestamp())
                            .build());

            log.info("Notification sent through WebSocket successfully");
        } catch (Exception e) {
            log.error("Error processing notification", e);
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.friend-notification.topic}",
            groupId = "${spring.kafka.friend-notification.group-id}",
            containerFactory = "sendFriendListenerContainerFactory")
    public void consumeFriendRequest(SendFriend sendFriend) {
        log.info("Received notification for userId: {} - {}", sendFriend.getSendId(), sendFriend.getReceiverId());

        try {
            sendFriendRepository.save(sendFriend);

            messagingTemplate.convertAndSend(
                    "/topic/messages/" + sendFriend.getSenderId(),
                    SendFriendResponse.builder()
                            .sendId(sendFriend.getSendId())
                            .senderId(sendFriend.getSenderId())
                            .receiverId(sendFriend.getReceiverId())
                            .condition(sendFriend.getCondition())
                            .build());

            log.info("Notification send through WebSocket successfully");
        } catch (Exception e) {
            log.error("Error processing notification ", e);
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.messaging-notification.topic}",
            groupId = "${spring.kafka.messaging-notification.group-id}",
            containerFactory = "messageKafkaListenerContainerFactory")
    public void listenerMessageNotification(MessagingResponse messagingResponse) {
        log.info("Received notification for userId: {}", messagingResponse.getSenderId());

        try {
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + messagingResponse.getSenderId(),
                    MessagingResponse.builder()
                            .id(messagingResponse.getId())
                            .senderId(messagingResponse.getSenderId())
                            .timestamp(messagingResponse.getTimestamp())
                            .attachmentUrl(messagingResponse.getAttachmentUrl())
                            .content(messagingResponse.getContent())
                            .conversationId(messagingResponse.getConversationId())
                            .messageType(messagingResponse.getMessageType())
                            .deliveryStatus(messagingResponse.getDeliveryStatus())
                            .isRead(messagingResponse.isRead())
                            .reactions(messagingResponse.getReactions())
                            .replyToId(messagingResponse.getReplyToId())
                            .build());

            log.info("Notification send through WebSocket successfully");
        } catch (Exception e) {
            log.error("Error processing notification ", e);
        }
    }
}

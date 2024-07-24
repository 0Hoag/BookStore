package com.example.notification.controller;


import com.example.event.dto.NotificationEvent;
import com.example.notification.dto.request.Recipient;
import com.example.notification.dto.request.SendEmailRequest;
import com.example.notification.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    EmailService emailService;
    @KafkaListener(topics = "notification-delivery")
    public void listenNotificationDelivery(NotificationEvent event){
        emailService.sendEmail(SendEmailRequest.builder()
                        .to(Recipient.builder()
                                .email(event.getRecipient())
                                .build())
                        .subject(event.getSubject())
                        .htmlContent(event.getBody())
                .build());
        log.info("Message: {}", event);
    }
}

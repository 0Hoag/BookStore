package com.example.notification.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendEmailRequest {
    Recipient to; // co thể setting list nếu muôn send đến nhiều email
    String htmlContent;
    String subject;
}

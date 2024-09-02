package com.example.identityservice.dto.request;

import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordVerifyRequest {
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;
}

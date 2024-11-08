package com.example.post_service.dto.response;

import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    String id;
    UserResponse user; // add 05/06/2024
    String firstName;
    String lastName;
    LocalDate dob;
    String city;
}

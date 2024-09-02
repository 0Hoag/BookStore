package com.example.identityservice.dto.request.response;

import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    String profileId;

    UserResponse user; // add 05/06/2024
    String email;
    String username;
    String firstName;
    String lastName;
    LocalDate dob;
}

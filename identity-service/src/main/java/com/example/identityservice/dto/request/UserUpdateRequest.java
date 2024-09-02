package com.example.identityservice.dto.request;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.Size;

import com.example.identityservice.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    String email;

    @DobConstraint(min = 10, message = "INVALID_DOB")
    LocalDate dob;

    List<String> roles;
    Set<String> cartItem;
    Set<String> orders;
}

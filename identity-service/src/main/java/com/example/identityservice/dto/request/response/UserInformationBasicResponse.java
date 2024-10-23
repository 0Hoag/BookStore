package com.example.identityservice.dto.request.response;

import com.example.identityservice.entity.UserImage;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInformationBasicResponse {
    String userId;
    String username;
    String firstName;
    String lastName;
    Set<UserImage> images;
}

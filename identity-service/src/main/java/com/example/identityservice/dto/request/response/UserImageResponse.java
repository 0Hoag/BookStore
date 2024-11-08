package com.example.identityservice.dto.request.response;

import com.example.identityservice.enums.ImageType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserImageResponse {
    String imageId;
    String imageUrl;
    ImageType imageType;

    String user;
}

package com.example.identityservice.entity;

import jakarta.persistence.*;

import com.example.identityservice.enums.ImageType;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class UserImage {
    @Id
    @Column(name = "image_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    String imageId;

    @Column(name = "image_url")
    String imageUrl;

    @Column(name = "image_type")
    ImageType imageType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    User user;
}

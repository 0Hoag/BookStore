package com.example.identityservice.entity;

import java.sql.Blob;
import java.util.Date;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Table(name = "image_table")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Lob
    Blob image;

    Date date = new Date();
}

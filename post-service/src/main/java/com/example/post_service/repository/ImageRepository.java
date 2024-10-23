package com.example.post_service.repository;

import com.example.post_service.entity.File;
import com.example.post_service.entity.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ImageRepository extends MongoRepository<Image, String> {
}

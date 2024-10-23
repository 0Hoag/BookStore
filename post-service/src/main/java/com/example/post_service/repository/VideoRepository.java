package com.example.post_service.repository;

import com.example.post_service.entity.Image;
import com.example.post_service.entity.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository extends MongoRepository<Video, String> {
}

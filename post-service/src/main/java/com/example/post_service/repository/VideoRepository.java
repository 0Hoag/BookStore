package com.example.post_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.post_service.entity.Video;

public interface VideoRepository extends MongoRepository<Video, String> {}

package com.example.post_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.post_service.entity.Image;

public interface ImageRepository extends MongoRepository<Image, String> {}

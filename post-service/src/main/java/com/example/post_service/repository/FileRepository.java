package com.example.post_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.post_service.entity.File;

public interface FileRepository extends MongoRepository<File, String> {}

package com.example.bookservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.bookservice.entity.Chapter;

public interface ChapterRepository extends MongoRepository<Chapter, String> {}

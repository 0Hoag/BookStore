package com.example.bookservice.repository;

import com.example.bookservice.entity.Chapter;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChapterRepository extends MongoRepository<Chapter, String> {
}

package com.example.post_service.repository;

import com.example.post_service.entity.PostStatusBook;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<PostStatusBook, String> {
}

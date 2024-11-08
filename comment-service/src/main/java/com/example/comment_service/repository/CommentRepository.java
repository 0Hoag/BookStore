package com.example.comment_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.comment_service.entity.Comment;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    //    Set<CommentResponse> deleteAll(Set<CommentResponse> commentResponses);
}

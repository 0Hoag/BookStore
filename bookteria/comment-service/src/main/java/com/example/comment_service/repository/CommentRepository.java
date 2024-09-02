package com.example.comment_service.repository;


import com.example.comment_service.dto.response.CommentResponse;
import com.example.comment_service.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
//    Set<CommentResponse> deleteAll(Set<CommentResponse> commentResponses);
}

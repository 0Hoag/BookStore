package com.example.Interaction_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.Interaction_service.entity.Like;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {}

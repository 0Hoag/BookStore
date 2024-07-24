package com.example.bookservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.bookservice.entity.BookProfile;

@Repository
public interface BookRepository extends MongoRepository<BookProfile, String> {}

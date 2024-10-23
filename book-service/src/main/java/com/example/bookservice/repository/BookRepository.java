package com.example.bookservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.bookservice.entity.BookProfile;

import java.awt.print.Book;

@Repository
public interface BookRepository extends MongoRepository<BookProfile, String> {
//    Page<Book> findAllByBookId(String bookId, Pageable pageable);
}

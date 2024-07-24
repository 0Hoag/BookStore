package com.example.bookservice.controller;

import java.io.IOException;
import java.util.List;

import com.example.bookservice.dto.request.*;
import org.springframework.web.bind.annotation.*;

import com.example.bookservice.dto.response.ApiResponse;
import com.example.bookservice.dto.response.BookResponse;
import com.example.bookservice.service.BookService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {
    BookService bookService;

    @PostMapping("/registration")
    public ApiResponse<BookResponse> createBook(
            @RequestBody CreateBookRequest request
    ) throws IOException {
        return ApiResponse.<BookResponse>builder()
                .code(1000)
                .result(bookService.createBook(request))
                .build();
    }

    @PostMapping("/registration/ManyChapter")
    public ApiResponse<BookResponse> createBookWithChapter(@RequestBody CreateBookWithChapterRequest chapter) {
        return ApiResponse.<BookResponse>builder()
                .code(1000)
                .result(bookService.createBookWithChapter(chapter))
                .build();
    }

    @PostMapping("/addChapter/{bookId}")
    public ApiResponse<BookResponse> addChapterWithBook(@PathVariable String bookId, @RequestBody AddChaptersRequest request) {
        return ApiResponse.<BookResponse>builder()
                .code(1000)
                .result(bookService.addChaptertoBook(bookId, request))
                .build();
    }

    @DeleteMapping("/removeChapter/{bookId}")
    public ApiResponse<BookResponse> removeChapterWithBook(@PathVariable String bookId, @RequestBody RemoveChapterRequest request) {
        return ApiResponse.<BookResponse>builder()
                .code(1000)
                .result(bookService.removeChapterBook(bookId, request))
                .build();
    }

    @PutMapping("/update/ManyChapter/{bookId}")
    public ApiResponse<BookResponse> updateBookWithChapter(@PathVariable String bookId, @RequestBody UpdateBookWithChapterRequest request) {
        return ApiResponse.<BookResponse>builder()
                .code(1000)
                .result(bookService.updateBookWithChapter(bookId, request))
                .build();
    }

    @GetMapping("/books/{bookId}")
    ApiResponse<BookResponse> getBook(@PathVariable String bookId) {
        return ApiResponse.<BookResponse>builder()
                .code(1000)
                .result(bookService.getBook(bookId))
                .build();
    }

    @GetMapping("/booksWithChapter/{bookId}")
    ApiResponse<BookResponse> getBookWithChapter(@PathVariable String bookId) {
        return ApiResponse.<BookResponse>builder()
                .code(1000)
                .result(bookService.getBookWithChapter(bookId))
                .build();
    }

    @GetMapping("/getAllBooks")
    ApiResponse<List<BookResponse>> getAllBook() {
        return ApiResponse.<List<BookResponse>>builder()
                .code(1000)
                .result(bookService.getAllBook())
                .build();
    }

    @DeleteMapping("/books/{bookId}")
    ApiResponse<BookResponse> deleteBook(@PathVariable String bookId) {
        bookService.deleteBook(bookId);
        return ApiResponse.<BookResponse>builder()
                .code(1000)
                .message("Delete Book Success")
                .build();
    }

    @PutMapping("/books/{bookId}")
    ApiResponse<BookResponse> updateBook(@PathVariable String bookId, @RequestBody UpdateBookRequest request) {
        return ApiResponse.<BookResponse>builder()
                .code(1000)
                .result(bookService.updateBook(bookId, request))
                .build();
    }
}

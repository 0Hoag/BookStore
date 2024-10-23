package com.example.bookservice.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.bookservice.dto.FileUtils;
import com.example.bookservice.dto.PageResponse;
import com.example.bookservice.dto.request.*;
import com.example.bookservice.dto.response.CreateChapterResponse;
import com.example.bookservice.entity.Chapter;
import com.example.bookservice.mapper.ChapterMapper;
import com.example.bookservice.repository.ChapterRepository;
import lombok.AllArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.bookservice.dto.response.BookResponse;
import com.example.bookservice.entity.BookProfile;
import com.example.bookservice.exception.AppException;
import com.example.bookservice.exception.ErrorCode;
import com.example.bookservice.mapper.BookMapper;
import com.example.bookservice.repository.BookRepository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookService {

    BookRepository bookRepository;
    BookMapper bookMapper;
    ChapterRepository chapterRepository;
    ChapterMapper chapterMapper;

    @NonFinal
    @Value("${cloudinary.cloud-name}")
    protected String cloudName;

    @NonFinal
    @Value("${cloudinary.api-key}")
    protected String apiKey;

    @NonFinal
    @Value("${cloudinary.api-secret}")
    protected String apiSecret;


    public BookResponse createBook(CreateBookRequest request) {
        BookProfile bookProfile = bookMapper.toBookProfile(request);
        bookProfile = bookRepository.save(bookProfile);
        return bookMapper.toBookResponse(bookProfile);
    }

    public BookResponse createBookWithChapter(CreateBookWithChapterRequest request) {
        var book = bookMapper.toBookWithChapter(request);

        Set<Chapter> chapters = selectedChapter(request.getChapters());
        book.setChapters(chapters);

        book = bookRepository.save(book);
        return bookMapper.toBookResponse(book);
    }

    public BookResponse addChaptertoBook(String bookId, AddChaptersRequest request) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));
        Set<Chapter> chapters = selectedChapter(request.getChapterIds());
        book.getChapters().addAll(chapters);

        return bookMapper.toBookResponse(bookRepository.save(book));
    }

    public BookResponse removeChapterBook(String bookId, RemoveChapterRequest request) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));
        Set<Chapter> chapters = selectedChapter(request.getChapterIds());
        book.getChapters().removeAll(chapters);

        return bookMapper.toBookResponse(bookRepository.save(book));
    }

    public BookResponse updateBookWithChapter(String bookId, UpdateBookWithChapterRequest request) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));
        Set<Chapter> chapters = selectedChapter(request.getChapters());
        bookMapper.updateBookWithChapter(book, request);
        book.setChapters(chapters);
        return bookMapper.toBookResponse(bookRepository.save(book));
    }

//    @PreAuthorize("hasRole('ADMIN')")

    public Set<Chapter> selectedChapter(Set<String> chapters) {
        Set<Chapter> chapters1 = chapterRepository.findAllById(chapters).stream()
                .collect(Collectors.toSet());
        return chapters1;
    }
    public BookResponse getBook(String bookId) {
        BookProfile bookProfile = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));

        return bookMapper.toBookResponse(bookRepository.save(bookProfile));
    }


    public BookResponse getBookWithChapter(String bookId) {
        BookProfile bookProfile = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));
        Set<Chapter> chapters = chapterRepository.findAll().stream().collect(Collectors.toSet());
        bookProfile.setChapters(chapters);
        bookProfile = bookRepository.save(bookProfile);
        BookResponse bookResponse = bookMapper.toBookResponse(bookProfile);

        return bookResponse;
    }

    public Map<String, Object> uploadPhoto(MultipartFile file) throws IOException {
        String fileName = FileUtils.generateFileName("File", FileUtils.getExtension(file.getName()));
        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        try {
            if (FileUtils.validateFile(file)) {
                file.transferTo(tempFile);

                Map<String, Object> uploadResult = cloudinaryConfig().uploader().upload(tempFile, ObjectUtils.emptyMap());
                return uploadResult;
            }else {
                throw new AppException(ErrorCode.UPLOAD_FILE_FAIL);
            }
        }catch (IOException e) {
            throw new AppException(ErrorCode.UPLOAD_FILE_FAIL);
        }finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<BookResponse> getAllBook(int page, int size) { // none check maybe fail
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<BookProfile> pageData = bookRepository.findAll(pageable);

        return PageResponse.<BookResponse>builder()
                .currentPage(page)
                .totalPages(pageData.getTotalPages())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream()
                        .map(bookMapper::toBookResponse)
                        .collect(Collectors.toList()))
                .build();
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBook(String bookId) {
        BookProfile bookProfile =
                bookRepository.findById(bookId).orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));
        chapterRepository.deleteAll(bookProfile.getChapters());
        bookRepository.deleteById(bookId);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public BookResponse updateBook(String bookId, UpdateBookRequest request) {
        BookProfile bookProfile =
                bookRepository.findById(bookId).orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));
        bookMapper.updateBook(bookProfile, request);
        return bookMapper.toBookResponse(bookRepository.save(bookProfile));
    }

    public BookResponse updateImageBook(String bookId, MultipartFile file) throws IOException {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));
        Map<String, Object> uploadResult = uploadPhoto(file);
        book.setImage( (String) uploadResult.get("url"));
        bookRepository.save(book);

        return bookMapper.toBookResponse(book);
    }

    @Bean
    public Cloudinary cloudinaryConfig() {
        Cloudinary cloudinary = null;
        Map config = new HashMap();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        cloudinary = new Cloudinary(config);
        return cloudinary;
    }
}

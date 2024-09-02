package com.example.bookservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.bookservice.dto.request.CreateChapterRequest;
import com.example.bookservice.dto.request.UpdateChapterRequest;
import com.example.bookservice.dto.response.CreateChapterResponse;
import com.example.bookservice.entity.Chapter;
import com.example.bookservice.exception.AppException;
import com.example.bookservice.exception.ErrorCode;
import com.example.bookservice.mapper.ChapterMapper;
import com.example.bookservice.repository.ChapterRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterService {
    ChapterRepository chapterRepository;
    ChapterMapper chapterMapper;

    public CreateChapterResponse createChapter(CreateChapterRequest request) {
        try {
            Chapter chapter = chapterMapper.toChapter(request);
            chapter = chapterRepository.save(chapter);
            return chapterMapper.toChapterResponse(chapter);
        } catch (AppException e) {
            throw new AppException(ErrorCode.UNCATEGORIZE_EXCEPTION);
        }
    }

    public CreateChapterResponse getChapter(String chapterId) {
        Chapter chapter = chapterRepository
                .findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZE_EXCEPTION));
        return chapterMapper.toChapterResponse(chapter);
    }

    public List<CreateChapterResponse> getAllChapter() {
        return chapterRepository.findAll().stream()
                .map(chapterMapper::toChapterResponse)
                .toList();
    }

    public void deleteChapter(String chaterId) {
        Chapter chapter = chapterRepository
                .findById(chaterId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZE_EXCEPTION));
        chapterRepository.deleteById(chapter.getChapterId());
    }

    public CreateChapterResponse updateChapter(String chapterId, UpdateChapterRequest request) {
        Chapter chapter = chapterRepository
                .findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZE_EXCEPTION));
        chapterMapper.updateChapter(chapter, request);
        return chapterMapper.toChapterResponse(chapterRepository.save(chapter));
    }
}

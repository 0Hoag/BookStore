package com.example.bookservice.mapper;

import com.example.bookservice.dto.request.CreateBookRequest;
import com.example.bookservice.dto.request.CreateChapterRequest;
import com.example.bookservice.dto.request.UpdateBookRequest;
import com.example.bookservice.dto.request.UpdateChapterRequest;
import com.example.bookservice.dto.response.BookResponse;
import com.example.bookservice.dto.response.CreateChapterResponse;
import com.example.bookservice.entity.BookProfile;
import com.example.bookservice.entity.Chapter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
    Chapter toChapter(CreateChapterRequest request);

    CreateChapterResponse toChapterResponse(Chapter entity);

    void updateChapter(@MappingTarget Chapter entity, UpdateChapterRequest request);
}

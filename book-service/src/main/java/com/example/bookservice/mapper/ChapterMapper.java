package com.example.bookservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.bookservice.dto.request.CreateChapterRequest;
import com.example.bookservice.dto.request.UpdateChapterRequest;
import com.example.bookservice.dto.response.CreateChapterResponse;
import com.example.bookservice.entity.Chapter;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
    Chapter toChapter(CreateChapterRequest request);

    CreateChapterResponse toChapterResponse(Chapter entity);

    void updateChapter(@MappingTarget Chapter entity, UpdateChapterRequest request);
}

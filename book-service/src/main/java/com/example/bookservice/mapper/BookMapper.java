package com.example.bookservice.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.example.bookservice.dto.request.CreateBookRequest;
import com.example.bookservice.dto.request.CreateBookWithChapterRequest;
import com.example.bookservice.dto.request.UpdateBookRequest;
import com.example.bookservice.dto.request.UpdateBookWithChapterRequest;
import com.example.bookservice.dto.response.BookResponse;
import com.example.bookservice.entity.BookProfile;
import com.example.bookservice.entity.Chapter;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookProfile toBookProfile(CreateBookRequest request);

    @Mapping(target = "chapters", source = "chapters", qualifiedByName = "mapChapterIdsToChapters")
    BookProfile toBookWithChapter(CreateBookWithChapterRequest request);

    BookResponse toBookResponse(BookProfile entity);

    BookResponse toBookResponseInfomationBasic(BookProfile entity);

    void updateBook(@MappingTarget BookProfile bookProfile, UpdateBookRequest request);

    @Mapping(target = "chapters", source = "chapters", qualifiedByName = "mapChapterIdsToChapters")
    void updateBookWithChapter(@MappingTarget BookProfile bookProfile, UpdateBookWithChapterRequest request);

    // convert String input to Chapter
    @Named("mapChapterIdsToChapters")
    default Set<Chapter> mapChapterIdsToChapters(Set<String> chapterIds) {
        if (chapterIds == null) {
            return new HashSet<>();
        }
        return chapterIds.stream()
                .map(chapterId -> {
                    Chapter chapter = new Chapter();
                    chapter.setChapterId(chapterId);
                    return chapter;
                })
                .collect(Collectors.toSet());
    }
}

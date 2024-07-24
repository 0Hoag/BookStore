package com.example.bookservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateChapterResponse {
    String chapterId;

    String chapterTitle;
    int sequenceNumber;
    String content;
}

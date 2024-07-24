package com.example.bookservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateChapterRequest {
    String chapterTitle;
    int sequenceNumber;
    String content;
}

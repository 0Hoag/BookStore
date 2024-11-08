package com.example.messaging_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.messaging_service.dto.response.ApiResponse;
import com.example.messaging_service.entity.ConversationParticipant;
import com.example.messaging_service.service.ConversationParticipantService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/participant")
public class ConversationParticipantController {
    ConversationParticipantService conversationParticipantService;

    @GetMapping("/getParticipantIds/{conversationId}")
    public ApiResponse<List<ConversationParticipant>> getParticipantIds(@PathVariable String conversationId) {
        return ApiResponse.<List<ConversationParticipant>>builder()
                .result(conversationParticipantService.getParticipantsForConversation(conversationId))
                .build();
    }
}

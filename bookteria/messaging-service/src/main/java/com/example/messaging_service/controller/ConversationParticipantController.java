package com.example.messaging_service.controller;

import com.example.messaging_service.dto.request.CreateMessagingRequest;
import com.example.messaging_service.dto.response.ApiResponse;
import com.example.messaging_service.dto.response.ConversationParticipantResponse;
import com.example.messaging_service.dto.response.MessagingResponse;
import com.example.messaging_service.dto.response.PageResponse;
import com.example.messaging_service.entity.ConversationListItem;
import com.example.messaging_service.entity.ConversationParticipant;
import com.example.messaging_service.service.ConversationParticipantService;
import com.example.messaging_service.service.MessagingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

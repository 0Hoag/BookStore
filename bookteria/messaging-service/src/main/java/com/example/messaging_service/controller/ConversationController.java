package com.example.messaging_service.controller;

import com.example.messaging_service.dto.request.CreateConversationRequest;
import com.example.messaging_service.dto.response.ApiResponse;
import com.example.messaging_service.dto.response.ConversationResponse;
import com.example.messaging_service.service.ConversationService;
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
@RequestMapping("/mess")
public class ConversationController {
    ConversationService conversationService;

    @PostMapping("/registration")
    ApiResponse<ConversationResponse> createConversation(@RequestBody CreateConversationRequest request) {
        return ApiResponse.<ConversationResponse>builder()
                .code(1000)
                .result(conversationService.createConversation(request))
                .build();
    }

    @GetMapping("/getUserConversations/{userId}")
    ApiResponse<List<ConversationResponse>> getUserConversations(@PathVariable String userId) {
        return ApiResponse.<List<ConversationResponse>>builder()
                .result(conversationService.getUserConversations(userId))
                .build();
    }
}

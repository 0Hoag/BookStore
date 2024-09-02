package com.example.messaging_service.controller;

import com.example.messaging_service.dto.request.CreateMessagingRequest;
import com.example.messaging_service.dto.response.ApiResponse;
import com.example.messaging_service.dto.response.MessagingResponse;
import com.example.messaging_service.dto.response.PageResponse;
import com.example.messaging_service.entity.ConversationListItem;
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
@RequestMapping("/messenger")
public class MessagingController {
    MessagingService messagingService;

    @PostMapping("/registration")
    public ApiResponse<MessagingResponse> createMessage(@RequestBody CreateMessagingRequest request) {
        return ApiResponse.<MessagingResponse>builder()
                .result(messagingService.sendMessage(request))
                .build();
    }

    @GetMapping("/getAllMess")
    public ApiResponse<List<MessagingResponse>> getAllMess() {
        return ApiResponse.<List<MessagingResponse>>builder()
                .code(1000)
                .result(messagingService.getAllMessage())
                .build();
    }

    @GetMapping("/getMessageForConversation")
    public ApiResponse<PageResponse<MessagingResponse>> getMessagesForConversation(
            @RequestParam(value = "conversationId", required = false) String conversationId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
            ) {
        return ApiResponse.<PageResponse<MessagingResponse>>builder()
                .code(1000)
                .result(messagingService.getMessagesForConversation(conversationId, page, size))
                .build();
    }

    @GetMapping("/getLassMessage/{conversationId}")
    public ApiResponse<MessagingResponse> getMessagesForConversation(@PathVariable String conversationId) {
        return ApiResponse.<MessagingResponse>builder()
                .code(1000)
                .result(messagingService.getLastMessage(conversationId))
                .build();
    }

    @GetMapping("/getUserConversationList")
    public ApiResponse<List<ConversationListItem>> getUserConversationList(
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "limit", required = false, defaultValue = "1") int limit
    ) {
        return ApiResponse.<List<ConversationListItem>>builder()
                .result(messagingService.getUserConversationList(userId, limit))
                .build();
    }
}

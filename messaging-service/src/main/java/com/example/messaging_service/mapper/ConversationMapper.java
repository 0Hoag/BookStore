package com.example.messaging_service.mapper;

import org.mapstruct.Mapper;

import com.example.messaging_service.dto.request.CreateConversationRequest;
import com.example.messaging_service.dto.response.ConversationResponse;
import com.example.messaging_service.entity.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    Conversation toConversation(CreateConversationRequest request);

    ConversationResponse toConversationResponse(Conversation entity);
}

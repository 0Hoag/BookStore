package com.example.messaging_service.mapper;

import com.example.messaging_service.dto.request.CreateConversationParticipantRequest;
import com.example.messaging_service.dto.request.CreateConversationRequest;
import com.example.messaging_service.dto.response.ConversationParticipantResponse;
import com.example.messaging_service.dto.response.ConversationResponse;
import com.example.messaging_service.entity.Conversation;
import com.example.messaging_service.entity.ConversationParticipant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConversationParticipantMapper {
    ConversationParticipant toConversationParticipant(CreateConversationParticipantRequest request);

    ConversationParticipantResponse toConversationParticipantResponse(ConversationParticipant entity);
}

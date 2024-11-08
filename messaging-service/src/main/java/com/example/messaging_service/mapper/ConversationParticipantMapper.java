package com.example.messaging_service.mapper;

import org.mapstruct.Mapper;

import com.example.messaging_service.dto.request.CreateConversationParticipantRequest;
import com.example.messaging_service.dto.response.ConversationParticipantResponse;
import com.example.messaging_service.entity.ConversationParticipant;

@Mapper(componentModel = "spring")
public interface ConversationParticipantMapper {
    ConversationParticipant toConversationParticipant(CreateConversationParticipantRequest request);

    ConversationParticipantResponse toConversationParticipantResponse(ConversationParticipant entity);
}

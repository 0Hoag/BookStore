package com.example.messaging_service.mapper;

import org.mapstruct.Mapper;

import com.example.messaging_service.dto.request.CreateMessagingRequest;
import com.example.messaging_service.dto.response.MessagingResponse;
import com.example.messaging_service.entity.Message;

@Mapper(componentModel = "spring")
public interface MessagingMapper {
    Message toMessaging(CreateMessagingRequest request);

    MessagingResponse toMessagingResponse(Message entity);

    //    MessagingResponse UpdateMessaging(String id, UpdateMessagingRequest request);
}

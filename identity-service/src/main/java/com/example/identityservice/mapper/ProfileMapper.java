package com.example.identityservice.mapper;

import com.example.identityservice.dto.request.response.UserProfileResponse;
import org.mapstruct.Mapper;

import com.example.identityservice.dto.request.ProfileCreationRequest;
import com.example.identityservice.dto.request.UserCreationRequest;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);

}

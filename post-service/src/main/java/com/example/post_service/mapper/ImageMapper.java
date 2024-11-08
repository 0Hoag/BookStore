package com.example.post_service.mapper;

import java.util.Base64;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.post_service.dto.request.CreateImageRequest;
import com.example.post_service.dto.response.ImageResponse;
import com.example.post_service.entity.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    Image toImage(CreateImageRequest request);

    @Mapping(target = "imageData", expression = "java(byteArrayToBase64(image.getImage()))")
    ImageResponse toImageResponse(Image image);

    default String byteArrayToBase64(byte[] image) {
        if (image == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(image);
    }
}

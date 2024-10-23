package com.example.identityservice.mapper;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.identityservice.dto.request.CreateImageRequest;
import com.example.identityservice.dto.request.response.ImageResponse;
import com.example.identityservice.entity.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    Image toImage(CreateImageRequest request);

    @Mapping(target = "imageData", expression = "java(blobToBase64(image.getImage()))")
    ImageResponse toImageResponse(Image image);

    default String blobToBase64(Blob blob) {
        try {
            if (blob == null) {
                return null;
            }
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (SQLException e) {
            throw new RuntimeException("Error converting Blob to Base64", e);
        }
    }
}

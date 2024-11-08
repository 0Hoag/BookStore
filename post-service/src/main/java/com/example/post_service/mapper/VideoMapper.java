package com.example.post_service.mapper;

import java.util.Base64;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.post_service.dto.request.CreateVideoRequest;
import com.example.post_service.dto.response.VideoResponse;
import com.example.post_service.entity.Video;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    Video toVideo(CreateVideoRequest request);

    @Mapping(target = "videoData", expression = "java(byteArrayToBase64(video.getVideo()))")
    VideoResponse toVideoResponse(Video video);

    default String byteArrayToBase64(byte[] video) {
        if (video == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(video);
    }
}

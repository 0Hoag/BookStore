package com.example.post_service.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.post_service.dto.response.VideoResponse;
import com.example.post_service.entity.Video;
import com.example.post_service.exception.AppException;
import com.example.post_service.exception.ErrorCode;
import com.example.post_service.mapper.VideoMapper;
import com.example.post_service.repository.VideoRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoService {
    VideoRepository videoRepository;
    VideoMapper videoMapper;

    public Video createVideo(MultipartFile file) throws IOException {
        if (file.getSize() > 16 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 16MB limit");
        }

        Video video = new Video();
        video.setVideo(file.getBytes());

        return videoRepository.save(video);
    }

    public VideoResponse viewVideo(String id) {
        var video = videoRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_EXISTED));
        return videoMapper.toVideoResponse(video);
    }

    public List<VideoResponse> viewAllVideo() {
        return videoRepository.findAll().stream()
                .map(videoMapper::toVideoResponse)
                .collect(Collectors.toList());
    }

    public void deleteVideo(String id) {
        videoRepository.deleteById(id);
    }
}

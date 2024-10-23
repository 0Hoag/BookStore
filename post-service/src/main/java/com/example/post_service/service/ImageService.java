package com.example.post_service.service;

import com.example.post_service.dto.response.ImageResponse;
import com.example.post_service.dto.response.PostResponse;
import com.example.post_service.entity.Image;
import com.example.post_service.exception.AppException;
import com.example.post_service.exception.ErrorCode;
import com.example.post_service.mapper.ImageMapper;
import com.example.post_service.repository.ImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageService {
    ImageRepository imageRepository;
    ImageMapper imageMapper;

    public ImageResponse createImage(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setImage(file.getBytes());

        image = imageRepository.save(image);

        return imageMapper.toImageResponse(image);
    }

    public ImageResponse viewImage(String id) {
        var image = imageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
        return imageMapper.toImageResponse(image);
    }

    public List<ImageResponse> viewAllImage() {
        return imageRepository.findAll().stream()
                .map(imageMapper::toImageResponse)
                .collect(Collectors.toList());
    }

    public void deleteImage(String id) {
        imageRepository.deleteById(id);
    }
}

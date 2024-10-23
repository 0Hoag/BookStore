package com.example.identityservice.service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.identityservice.dto.request.response.ImageResponse;
import com.example.identityservice.entity.Image;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapper.ImageMapper;
import com.example.identityservice.repository.ImageRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageService {
    ImageRepository imageRepository;
    ImageMapper imageMapper;

    public ImageResponse createImage(MultipartFile file) throws IOException, SQLException {
        byte[] bytes = file.getBytes();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

        Image image = new Image();
        image.setImage(blob);

        imageRepository.save(image);

        return imageMapper.toImageResponse(image);
    }

    public ImageResponse viewImage(String id) {
        var image = imageRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
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

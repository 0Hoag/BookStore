package com.example.post_service.dto;

import java.util.*;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_VIDEO_SIZE = 80 * 1024 * 1024; // 50MB
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "jfif");
    private static final List<String> ALLOWED_VIDEO_EXTENSIONS = Arrays.asList("mp4", "avi", "mov");

    public static Boolean validateFile(MultipartFile file) {
        return validateFileSize(file) && validateFileExtension(file);
    }

    public static Boolean validateFileSize(MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        if (ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            return file.getSize() <= MAX_IMAGE_SIZE;
        } else if (ALLOWED_VIDEO_EXTENSIONS.contains(extension)) {
            return file.getSize() <= MAX_VIDEO_SIZE;
        }
        return false;
    }

    public static Boolean validateFileExtension(MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        return ALLOWED_IMAGE_EXTENSIONS.contains(extension) || ALLOWED_VIDEO_EXTENSIONS.contains(extension);
    }

    public static boolean isVideoFile(String filename) {
        String extension = getExtension(filename);
        return ALLOWED_VIDEO_EXTENSIONS.contains(extension);
    }

    public static String getExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex > 0) ? filename.substring(lastDotIndex + 1).toLowerCase() : "";
    }

    public static String generateFileName(String prefix, String extension) {
        return prefix + "_" + UUID.randomUUID() + "." + extension;
    }
}

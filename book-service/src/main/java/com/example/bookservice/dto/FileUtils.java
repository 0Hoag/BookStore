package com.example.bookservice.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "jfif", "webp");

    public static Boolean validateFile(MultipartFile file) {
        return validateFileSize(file) && validateFileExtension(file);
    }

    public static Boolean validateFileSize(MultipartFile file) {
        return file.getSize() <= MAX_FILE_SIZE;
    }

    public static Boolean validateFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            String fileExtension = originalFilename
                    .substring(originalFilename.lastIndexOf(".") + 1)
                    .toLowerCase();
            return ALLOWED_EXTENSIONS.contains(fileExtension);
        }
        return false;
    }

    public static String generateFileName(String prefix, String extension) {
        return prefix + "_" + UUID.randomUUID() + "." + extension;
    }

    public static String getExtension(String filename) {
        return Optional.ofNullable(filename)
                .map(f -> {
                    int pos = f.lastIndexOf(".");
                    return pos > 0 ? f.substring(pos + 1) : "";
                })
                .orElse("");
    }
}

package com.example.HomeReno.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private final String uploadDir;
    private final String urlPrefix;

    public FileStorageService(
            @Value("${storage.upload-dir:uploads}") String uploadDir,
            @Value("${storage.url-prefix:/uploads/}") String urlPrefix) {
        this.uploadDir = uploadDir;
        this.urlPrefix = urlPrefix;
    }

    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("file must be an image");
        }
        String originalFilename = file.getOriginalFilename();
        String cleanedFilename = originalFilename == null ? "" : StringUtils.cleanPath(originalFilename);
        String extension = "";
        int dotIndex = cleanedFilename.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < cleanedFilename.length() - 1) {
            extension = cleanedFilename.substring(dotIndex);
        }
        String filename = UUID.randomUUID() + extension;
        Path uploadPath = getUploadPath();
        try {
            Files.createDirectories(uploadPath);
            Path target = uploadPath.resolve(filename).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store image file", ex);
        }
        return normalizeUrlPrefix(urlPrefix) + filename;
    }

    public void deleteIfLocal(String url) {
        if (url == null || url.isBlank()) {
            return;
        }
        String prefix = normalizeUrlPrefix(urlPrefix);
        int index = url.indexOf(prefix);
        if (index < 0) {
            return;
        }
        String filename = url.substring(index + prefix.length());
        if (filename.isBlank()) {
            return;
        }
        Path uploadPath = getUploadPath();
        Path target = uploadPath.resolve(filename).normalize();
        if (!target.startsWith(uploadPath)) {
            return;
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            return;
        }
    }

    private Path getUploadPath() {
        String dir = uploadDir == null || uploadDir.isBlank() ? "uploads" : uploadDir;
        return Paths.get(dir).toAbsolutePath().normalize();
    }

    private String normalizeUrlPrefix(String prefix) {
        String value = prefix == null || prefix.isBlank() ? "/uploads/" : prefix;
        if (!value.startsWith("/")) {
            value = "/" + value;
        }
        if (!value.endsWith("/")) {
            value = value + "/";
        }
        return value;
    }
}

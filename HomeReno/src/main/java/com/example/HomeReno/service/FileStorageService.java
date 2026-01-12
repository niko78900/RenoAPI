package com.example.HomeReno.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.imageio.ImageIO;

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
        if (contentType != null && !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("file must be an image");
        }
        byte[] bytes = readBytes(file);
        validateImageBytes(bytes);
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
            Files.write(target, bytes);
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

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read image file", ex);
        }
    }

    private void validateImageBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("file is required");
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new IllegalArgumentException("file must be a valid image");
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("file must be a valid image", ex);
        }
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

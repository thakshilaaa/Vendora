package com.vendora.epic6.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * Stores supplier-uploaded product images to the local file system.
 * Files are exposed publicly via /uploads/** (see WebMvcConfig).
 */
@Service
public class FileUploadService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() throws IOException {
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

    /**
     * @return public URL path that can be referenced from img src,
     *         e.g. "/uploads/abc123.jpg"
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.'))
                : ".bin";

        String filename = UUID.randomUUID() + ext.toLowerCase();
        Path target = Paths.get(uploadDir).resolve(filename);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not save uploaded file", e);
        }

        return "/uploads/" + filename;
    }
}

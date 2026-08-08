package com.jobportal.job_portal_backend.service.impl;

import com.jobportal.job_portal_backend.exception.FileStorageException;
import com.jobportal.job_portal_backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("pdf", "doc", "docx");

    @Override
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Please upload a resume file");
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : ""
        );
        String extension = getExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileStorageException("Only PDF, DOC, and DOCX files are allowed");
        }

        String storedFilename = UUID.randomUUID() + "." + extension;

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Path targetPath = uploadPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + e.getMessage());
        }

        return storedFilename;
    }

    private String getExtension(String filename) {
        if (filename.isBlank() || !filename.contains(".")) {
            throw new FileStorageException("Invalid file name");
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
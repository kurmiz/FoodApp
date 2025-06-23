package com.quickbite.service;

import com.quickbite.util.ConsoleLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service for handling image uploads and management
 */
@Service
public class ImageService {

    private static final String UPLOAD_DIR = "uploads/images/";
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final ConsoleLogger logger;

    @Autowired
    public ImageService(ConsoleLogger logger) {
        this.logger = logger;
        createUploadDirectory();
    }

    /**
     * Create upload directory if it doesn't exist
     */
    private void createUploadDirectory() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.logSystemEvent("UPLOAD_DIRECTORY_CREATED", "Created directory: " + UPLOAD_DIR);
            }
        } catch (IOException e) {
            logger.logSystemEvent("UPLOAD_DIRECTORY_ERROR", "Failed to create upload directory: " + e.getMessage());
        }
    }

    /**
     * Save uploaded image and return filename
     */
    public String saveImage(InputStream inputStream, String originalFilename) throws IOException {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }

        // Validate file extension
        if (!isValidImageFile(originalFilename)) {
            throw new IllegalArgumentException("Invalid file type. Allowed: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        // Generate unique filename
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        // Save file
        Path filePath = Paths.get(UPLOAD_DIR + uniqueFilename);
        
        try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytes = 0;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytes += bytesRead;
                if (totalBytes > MAX_FILE_SIZE) {
                    // Delete partially uploaded file
                    Files.deleteIfExists(filePath);
                    throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
                }
                outputStream.write(buffer, 0, bytesRead);
            }
            
            logger.logUserAction("IMAGE_UPLOADED", 
                String.format("Uploaded image: %s -> %s (%.2f KB)", 
                    originalFilename, uniqueFilename, totalBytes / 1024.0));
            
            return uniqueFilename;
        } catch (IOException e) {
            // Clean up on error
            Files.deleteIfExists(filePath);
            throw e;
        }
    }

    /**
     * Get image URL for display
     */
    public String getImageUrl(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return getDefaultImageUrl();
        }
        return "/images/" + filename;
    }

    /**
     * Get default image URL for items without images
     */
    public String getDefaultImageUrl() {
        return "/images/default-food.jpg";
    }

    /**
     * Delete image file
     */
    public boolean deleteImage(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        try {
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                logger.logUserAction("IMAGE_DELETED", "Deleted image: " + filename);
            }
            
            return deleted;
        } catch (IOException e) {
            logger.logSystemEvent("IMAGE_DELETE_ERROR", "Failed to delete image " + filename + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if file has valid image extension
     */
    private boolean isValidImageFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }

    /**
     * Get file size in a readable format
     */
    public String getReadableFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    /**
     * Check if image file exists
     */
    public boolean imageExists(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        return Files.exists(filePath);
    }

    /**
     * Get upload directory path
     */
    public String getUploadDirectory() {
        return UPLOAD_DIR;
    }

    /**
     * Get maximum file size
     */
    public long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }

    /**
     * Get allowed file extensions
     */
    public String[] getAllowedExtensions() {
        return ALLOWED_EXTENSIONS.clone();
    }
}

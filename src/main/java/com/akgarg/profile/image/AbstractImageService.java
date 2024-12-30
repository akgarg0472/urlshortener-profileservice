package com.akgarg.profile.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

abstract class AbstractImageService implements ImageService {

    public String generateImageId(final MultipartFile image) {
        final var imageHash = Objects.hash(image);
        final var randomStringHash = Objects.hash(UUID.randomUUID().toString());
        return Math.abs(randomStringHash) + "" + System.nanoTime() + Math.abs(imageHash);
    }

    public File convertMultipartFileToFile(final MultipartFile file, final String fileName) throws IOException {
        final var convertedFile = new File(fileName);
        file.transferTo(convertedFile);
        return convertedFile;
    }

    protected String extractFileExtension(final String originalFileName) {
        final var lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return originalFileName.substring(lastDotIndex + 1);
        }
        return "jpg";
    }

}

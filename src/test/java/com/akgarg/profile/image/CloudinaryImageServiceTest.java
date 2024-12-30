package com.akgarg.profile.image;

import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CloudinaryImageServiceTest {

    private CloudinaryImageService imageService;

    @BeforeEach
    void setUp() {
        final var environment = new AbstractEnvironment() {
            @Override
            public String getProperty(@Nonnull final String key) {
                return super.getProperty(key);
            }
        };
        imageService = new CloudinaryImageService(environment);
    }

    @Test
    void testUploadImage() {
        assertNotNull(imageService, "Image service is null");

        final MultipartFile image = getImage();
        assertNotNull(image, "Image file to upload is null");

        final Optional<String> uploadImageUrl = imageService.uploadImage(null, image);
        assertThat(uploadImageUrl).isNotEmpty();
    }

    private MultipartFile getImage() {
        try {
            final var path = Path.of("C:" + File.separator + "tmp" + File.separator + "airplane_1.jpg");
            return new MockMultipartFile("test.jpg", Files.readAllBytes(path));
        } catch (IOException e) {
            return null;
        }
    }

}

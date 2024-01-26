package com.akgarg.profile.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CloudImageServiceTest {

    private CloudImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new CloudImageService();
    }

    @Test
    void testUploadImage() {
        assertNotNull(imageService, "Image service is null");

        final MultipartFile image = getImage();
        assertNotNull(image, "Image file to upload is null");

        final Optional<String> uploadImageUrl = imageService.uploadImage(image);
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

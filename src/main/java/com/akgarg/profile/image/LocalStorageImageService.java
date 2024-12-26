package com.akgarg.profile.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class LocalStorageImageService extends AbstractImageService implements InitializingBean {

    private static final String TMP_IMAGE_DIR = File.separator +
            "tmp" + File.separator +
            "urlshortener" + File.separator +
            "profile-pictures";

    @Override
    public void afterPropertiesSet() throws IOException {
        try {
            final Path path = Path.of(TMP_IMAGE_DIR);

            if (!Files.exists(path)) {
                log.info("Creating temporary image directory: {}", TMP_IMAGE_DIR);
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            log.error("Error creating base directory to store profile pictures", e);
            throw e;
        }
    }

    @Override
    public Optional<String> uploadImage(final MultipartFile image) {
        Objects.requireNonNull(image, "Image can't be null");

        try {
            final String imageId = generateImageId(image);
            final String fileExtension = extractFileExtension(Objects.requireNonNull(image.getOriginalFilename()));
            final String fileName = TMP_IMAGE_DIR + File.separator + imageId + "." + fileExtension;
            final File convertedFile = convertMultipartFileToFile(image, fileName);
            storeFile(convertedFile);
            return Optional.of(convertedFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Error storing image: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void deleteImage(final String imageUrl) {
        final Path path = Path.of(TMP_IMAGE_DIR + File.separator + imageUrl);
        try {
            final boolean fileDeleted = Files.deleteIfExists(path);
            log.info("Image file with id={} delete result: {}", imageUrl, fileDeleted);
        } catch (IOException e) {
            log.error("Error deleting image with id: {}", imageUrl);
        }
    }

    private void storeFile(final File file) throws IOException {
        final Path destinationPath = Path.of(TMP_IMAGE_DIR, file.getName());
        Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
    }

}

package com.akgarg.profile.image;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

public class LocalStorageImageService extends AbstractImageService implements InitializingBean {

    private static final Logger LOGGER = LogManager.getLogger(LocalStorageImageService.class);
    private static final String IMAGE_DIRECTORY = "C:" + File.separator + "tmp" + File.separator + "profile-pictures";

    @Override
    public void afterPropertiesSet() {
        try {
            final Path path = Path.of(IMAGE_DIRECTORY);

            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (Exception e) {
            LOGGER.error("Error creating base directory to store profile pictures", e);
        }
    }

    @Override
    public Optional<String> uploadImage(final MultipartFile image) {
        Objects.requireNonNull(image, "Image can't be null");

        try {
            final String imageId = generateImageId(image);
            final String fileExtension = extractFileExtension(Objects.requireNonNull(image.getOriginalFilename()));
            final String fileName = IMAGE_DIRECTORY + File.separator + imageId + "." + fileExtension;
            final File convertedFile = convertMultipartFileToFile(image, fileName);
            storeFile(convertedFile);
            return Optional.of(convertedFile.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Error storing image: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void deleteImage(final String imageUrl) {
        final Path path = Path.of(IMAGE_DIRECTORY + File.separator + imageUrl);
        try {
            final boolean fileDeleted = Files.deleteIfExists(path);
            LOGGER.info("Image file with id={} delete result: {}", imageUrl, fileDeleted);
        } catch (IOException e) {
            LOGGER.error("Error deleting image with id: {}", imageUrl);
        }
    }

    private void storeFile(final File file) throws IOException {
        final Path destinationPath = Path.of(IMAGE_DIRECTORY, file.getName());
        Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
    }

}

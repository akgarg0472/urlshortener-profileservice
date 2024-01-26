package com.akgarg.profile.image;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CloudImageService extends AbstractImageService {

    private static final Logger LOGGER = LogManager.getLogger(CloudImageService.class);
    private final Cloudinary cloudinary;

    public CloudImageService() {
        cloudinary = new Cloudinary(getCloudinaryConfigs());
    }

    private Map<String, String> getCloudinaryConfigs() {
        final Dotenv dotenv = Dotenv.load();
        return Map.of(
                "cloud_name", Objects.requireNonNull(dotenv.get("CLOUD_IMAGE_SERVICE_CLOUD_NAME"), "Image cloud name is null"),
                "api_key", Objects.requireNonNull(dotenv.get("CLOUD_IMAGE_SERVICE_API_KEY"), "Image cloud service api key is null"),
                "api_secret", Objects.requireNonNull(dotenv.get("CLOUD_API_SERVICE_SECRET_KEY"), "Image cloud service secret is null")
        );
    }

    @Override
    public Optional<String> uploadImage(final MultipartFile image) {
        final String imageId = generateImageId(image);

        try {
            final Map<String, String> uploadOptions = Map.of(
                    "folder", "profile-pictures",
                    "public_id", imageId
            );

            final Map<?, ?> uploadResult = cloudinary
                    .uploader()
                    .upload(image.getBytes(), uploadOptions);

            return Optional.of(Objects.requireNonNull(uploadResult.get("secure_url")).toString());
        } catch (IOException e) {
            LOGGER.error("Error uploading image file to cloudinary", e);
            return Optional.empty();
        }
    }

    @Override
    public void deleteImage(final String imageId) {
        // implement later
    }

}

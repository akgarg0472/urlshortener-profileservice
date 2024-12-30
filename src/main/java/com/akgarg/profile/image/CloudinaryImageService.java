package com.akgarg.profile.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public class CloudinaryImageService extends AbstractImageService {

    private static final String FOLDER_NAME = "profile-pictures";
    private final Cloudinary cloudinary;

    public CloudinaryImageService(final Environment environment) {
        this.cloudinary = new Cloudinary(Objects.requireNonNull(environment.getProperty("CLOUDINARY_URL"), "CLOUDINARY_URL is required"));
    }

    @Override
    public Optional<String> uploadImage(final Object requestId, final MultipartFile image) {
        final var imageId = generateImageId(image);

        try {
            final var uploadOptions = ObjectUtils.asMap(
                    "folder", FOLDER_NAME,
                    "public_id", imageId,
                    "overwrite", true
            );

            final var uploadResult = cloudinary.uploader().upload(image.getBytes(), uploadOptions);
            return Optional.of(Objects.requireNonNull(uploadResult.get("secure_url")).toString());
        } catch (Exception e) {
            log.error("[{}] Error uploading image file to cloudinary", requestId, e);
            return Optional.empty();
        }
    }

    @Override
    public void deleteImage(final Object requestId, final String imageUrl) {
        try {
            final var imageId = extractImageId(imageUrl);

            if (imageId != null) {
                final var deleteResult = cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap()).get("result");

                if (deleteResult != null && deleteResult.equals("ok")) {
                    log.info("[{}] Deleted image: {}", requestId, imageUrl);
                }
            }
        } catch (Exception e) {
            log.error("[{}] Error deleting image from cloudinary", requestId, e);
        }
    }

    private String extractImageId(final String imageUrl) {
        if (imageUrl == null || imageUrl.contains("default")) {
            return null;
        }

        final var slashIndex = imageUrl.lastIndexOf('/');
        final var dotIndex = imageUrl.lastIndexOf('.');

        if (slashIndex != -1 && dotIndex != -1) {
            return FOLDER_NAME + "/" + imageUrl.substring(slashIndex + 1, dotIndex);
        }

        return null;
    }

}

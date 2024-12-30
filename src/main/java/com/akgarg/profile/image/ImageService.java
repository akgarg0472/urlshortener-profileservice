package com.akgarg.profile.image;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageService {

    Optional<String> uploadImage(Object requestId, MultipartFile image);

    void deleteImage(Object requestId, String imageUrl);

}

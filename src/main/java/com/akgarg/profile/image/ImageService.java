package com.akgarg.profile.image;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageService {

    Optional<String> uploadImage(MultipartFile image);

    void deleteImage(String imageUrl);

}

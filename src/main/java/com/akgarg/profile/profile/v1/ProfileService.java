package com.akgarg.profile.profile.v1;

import com.akgarg.profile.db.DatabaseService;
import com.akgarg.profile.exception.BadRequestException;
import com.akgarg.profile.exception.ResourceNotFoundException;
import com.akgarg.profile.image.ImageService;
import com.akgarg.profile.notification.NotificationService;
import com.akgarg.profile.request.UpdatePasswordRequest;
import com.akgarg.profile.request.UpdateProfileRequest;
import com.akgarg.profile.response.DeleteResponse;
import com.akgarg.profile.response.UpdateResponse;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("LoggingSimilarMessage")
public class ProfileService {

    private final NotificationService notificationService;
    private final DatabaseService databaseService;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    public Optional<ProfileDTO> getProfileByProfileId(@Nonnull final String profileId) {
        checkProfileId(profileId);

        log.info("Get profile request received for id {}", profileId);

        final var profile = getProfileById(profileId);

        return Optional.of(ProfileDTO.fromProfile(profile));
    }

    public DeleteResponse deleteProfile(@Nonnull final String profileId) {
        checkProfileId(profileId);

        log.info("Delete profile request received for id {}", profileId);

        getProfileById(profileId);

        databaseService.deleteProfileById(profileId);

        log.info("Profile deleted successfully with id {}", profileId);
        return new DeleteResponse(HttpStatus.OK.value(), "Profile deleted successfully");
    }

    public UpdateResponse updateProfile(@Nonnull final String profileId, @Nonnull final UpdateProfileRequest request) {
        checkProfileId(profileId);

        checkUpdateProfileRequestForAllNullValues(request);

        log.info("Update Profile request received for id {}", profileId);

        if (log.isDebugEnabled()) {
            log.debug("Received request: {}", request);
        }

        final var profile = getProfileById(profileId);

        boolean isProfileDataUpdated = false;

        if (isValidProfilePicture(request.getProfilePicture())) {
            final var updatedProfilePictureUrl = imageService.uploadImage(request.getProfilePicture());
            final var previousProfilePicture = profile.getProfilePictureUrl();

            if (updatedProfilePictureUrl.isPresent()) {
                isProfileDataUpdated = true;
                log.debug("Profile picture uploaded successfully for id {}", profileId);
                profile.setProfilePictureUrl(updatedProfilePictureUrl.get());
                imageService.deleteImage(previousProfilePicture);
            }
        }

        if (isUpdateParamValid(request.getName(), profile.getName())) {
            profile.setName(request.getName().trim());
            isProfileDataUpdated = true;
            log.debug("Profile name updated");
        }

        if (isUpdateParamValid(request.getBio(), profile.getBio())) {
            profile.setBio(request.getBio().trim());
            isProfileDataUpdated = true;
            log.debug("Profile bio updated");
        }

        if (isUpdateParamValid(request.getPhone(), profile.getPhone())) {
            profile.setPhone(request.getPhone().trim());
            isProfileDataUpdated = true;
            log.debug("Profile phone number updated");
        }

        if (isUpdateParamValid(request.getCity(), profile.getCity())) {
            profile.setCity(request.getCity().trim());
            isProfileDataUpdated = true;
            log.debug("Profile city updated");
        }

        if (isUpdateParamValid(request.getState(), profile.getState())) {
            profile.setState(request.getState().trim());
            isProfileDataUpdated = true;
            log.debug("Profile state updated");
        }

        if (isUpdateParamValid(request.getCountry(), profile.getCountry())) {
            profile.setCountry(request.getCountry().trim());
            isProfileDataUpdated = true;
            log.debug("Profile country updated");
        }

        if (isUpdateParamValid(request.getZipcode(), profile.getZipcode())) {
            profile.setZipcode(request.getZipcode().trim());
            isProfileDataUpdated = true;
            log.debug("Profile zipcode updated");
        }

        if (isUpdateParamValid(request.getBusinessDetails(), profile.getBusinessDetails())) {
            profile.setBusinessDetails(request.getBusinessDetails().trim());
            isProfileDataUpdated = true;
            log.debug("Profile business details updated");
        }

        if (isProfileDataUpdated) {
            databaseService.updateProfile(profile);
            log.info("Profile updated successfully for id {}", profileId);
            return UpdateResponse.ok("Profile successfully updated");
        }

        return UpdateResponse.ok("Profile unchanged because no updates were made");
    }

    private boolean isValidProfilePicture(final MultipartFile profilePicture) {
        if (profilePicture == null) {
            return false;
        }
        final var contentType = profilePicture.getContentType();
        return contentType != null && contentType.startsWith("image");
    }

    private void checkUpdateProfileRequestForAllNullValues(final UpdateProfileRequest request) {
        final var requestClass = UpdateProfileRequest.class;
        final var fields = requestClass.getDeclaredFields();

        final boolean allFieldsNull = Arrays.stream(fields)
                .filter(field -> {
                    try {
                        field.trySetAccessible();
                        return field.get(request) == null;
                    } catch (IllegalAccessException e) {
                        return true;
                    }
                })
                .count() == fields.length;

        if (allFieldsNull) {
            throw new BadRequestException(new String[]{"All fields are null in request"}, "Invalid request");
        }
    }

    public UpdateResponse updatePassword(@Nonnull final String profileId, @Nonnull final UpdatePasswordRequest updatePasswordRequest) {
        checkProfileId(profileId);

        log.info("Update password request received for id {}", profileId);

        if (log.isDebugEnabled()) {
            log.debug("Received request: {}", updatePasswordRequest);
        }

        if (!arePasswordsEqual(updatePasswordRequest)) {
            log.info("New password and confirm password mismatched");
            return UpdateResponse.badRequest("New password and confirm password mismatched");
        }

        final var profile = getProfileById(profileId);

        if (isOAuthLoginType(profile.getUserLoginType())) {
            log.info("User login type is {} which is not allowed", profile.getUserLoginType());
            return UpdateResponse.badRequest("OAuth login profile is not allowed to reset password");
        }

        if (!matchPassword(updatePasswordRequest.currentPassword(), profile.getPassword())) {
            if (log.isDebugEnabled()) {
                log.debug("Incorrect password provided");
            }
            return UpdateResponse.badRequest("Incorrect current password");
        }

        final var encryptedPassword = encryptPassword(updatePasswordRequest.newPassword());
        databaseService.updatePassword(profileId, encryptedPassword);

        log.info("Password updated successfully");
        notificationService.sendPasswordChangedSuccessEmail(profile.getEmail(), profile.getName());
        return UpdateResponse.ok("Password updated successfully");
    }

    private Profile getProfileById(@Nonnull final String profileId) {
        return databaseService.findByProfileId(profileId)
                .orElseThrow(() -> {
                    log.info("Profile not found for id {}", profileId);
                    return new ResourceNotFoundException("Profile not found for id: " + profileId);
                });
    }

    private boolean arePasswordsEqual(@Nonnull final UpdatePasswordRequest request) {
        return Objects.equals(request.newPassword(), request.confirmPassword());
    }

    private String encryptPassword(@Nonnull final String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private boolean matchPassword(@Nonnull final String rawPassword, @Nonnull final String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    private void checkProfileId(final String profileId) {
        if (profileId == null || profileId.isBlank()) {
            throw new BadRequestException(new String[]{"Invalid profile id provided: " + profileId}, "Bad Request");
        }
    }

    private boolean isUpdateParamValid(final String reqParam, final String profileParam) {
        return reqParam != null && !Objects.equals(reqParam, profileParam);
    }

    private boolean isOAuthLoginType(final String loginType) {
        return loginType != null && !loginType.isBlank() && loginType.toLowerCase().contains("oauth");
    }

}

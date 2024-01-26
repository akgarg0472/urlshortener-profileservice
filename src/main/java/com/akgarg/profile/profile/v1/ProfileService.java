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
import com.google.common.base.Objects;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

import static com.akgarg.profile.utils.ProfileUtils.extractRequestIdFromRequest;

@Service
public class ProfileService {

    private static final Logger LOGGER = LogManager.getLogger(ProfileService.class);
    private static final int PASSWORD_ENCODER_STRENGTH = 10;

    private final DatabaseService databaseService;
    private final ImageService imageService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(
            @Nonnull final DatabaseService databaseService,
            @Nonnull final ImageService imageService,
            @Nonnull final NotificationService notificationService
    ) {
        this.databaseService = databaseService;
        this.imageService = imageService;
        this.notificationService = notificationService;
        this.passwordEncoder = new BCryptPasswordEncoder(PASSWORD_ENCODER_STRENGTH);
    }

    public Optional<ProfileDTO> getProfileByProfileId(
            @Nonnull final HttpServletRequest httpRequest,
            @Nonnull final String profileId
    ) {
        checkProfileId(profileId);

        final var requestId = extractRequestIdFromRequest(httpRequest);

        LOGGER.debug("[{}] get profile received for profile id: {}", requestId, profileId);

        final Optional<Profile> profile = databaseService.findByProfileId(profileId);

        if (profile.isEmpty()) {
            LOGGER.error("[{}] profile not found by id: {}", requestId, profileId);
            return Optional.empty();
        }

        return Optional.of(ProfileDTO.fromProfile(profile.get()));
    }

    public DeleteResponse deleteProfile(
            @Nonnull final HttpServletRequest httpRequest,
            @Nonnull final String profileId
    ) {
        checkProfileId(profileId);

        final var requestId = extractRequestIdFromRequest(httpRequest);

        LOGGER.info("[{}] Profile delete request for id={} received", requestId, profileId);

        getProfileById(requestId, profileId);

        final boolean profileDeleted = databaseService.deleteProfileById(profileId);

        if (profileDeleted) {
            LOGGER.info("[{}] profile deleted successfully with id={}", requestId, profileId);
            return new DeleteResponse(HttpStatus.OK.value(), "Profile deleted successfully");
        }

        return new DeleteResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting profile for id: " + profileId);
    }

    public UpdateResponse updateProfile(
            @Nonnull final HttpServletRequest httpRequest,
            @Nonnull final String profileId,
            @Nonnull final UpdateProfileRequest request
    ) {
        checkProfileId(profileId);
        checkUpdateProfileRequestForAllNullValues(request);

        final var requestId = extractRequestIdFromRequest(httpRequest);

        LOGGER.info("[{}] Profile update request for id={} received: {}", requestId, profileId, request);

        final Profile profile = getProfileById(requestId, profileId);

        boolean isProfileDataUpdated = false;

        if (isValidProfilePicture(request.getProfilePicture())) {
            final var updatedProfilePictureUrl = imageService.uploadImage(request.getProfilePicture());
            if (updatedProfilePictureUrl.isPresent()) {
                isProfileDataUpdated = true;
                LOGGER.info("[{}] profile picture uploaded successfully for profileId={}", requestId, profileId);
                profile.setProfilePictureUrl(updatedProfilePictureUrl.get());
            }
        }

        if (isUpdateParamValid(request.getName(), profile.getName())) {
            profile.setName(request.getName().trim());
            isProfileDataUpdated = true;
            LOGGER.debug("[{}] profile name updated", requestId);
        }

        if (isUpdateParamValid(request.getBio(), profile.getBio())) {
            profile.setBio(request.getBio().trim());
            isProfileDataUpdated = true;
            LOGGER.debug("[{}] profile bio updated", requestId);
        }

        if (isUpdateParamValid(request.getPhone(), profile.getPhone())) {
            profile.setPhone(request.getPhone().trim());
            isProfileDataUpdated = true;
            LOGGER.debug("[{}] profile phone updated", requestId);
        }

        if (isUpdateParamValid(request.getCity(), profile.getCity())) {
            profile.setCity(request.getCity().trim());
            isProfileDataUpdated = true;
            LOGGER.debug("[{}] profile city updated", requestId);
        }

        if (isUpdateParamValid(request.getState(), profile.getState())) {
            profile.setState(request.getState().trim());
            isProfileDataUpdated = true;
            LOGGER.debug("[{}] profile state updated", requestId);
        }

        if (isUpdateParamValid(request.getCountry(), profile.getCountry())) {
            profile.setCountry(request.getCountry().trim());
            isProfileDataUpdated = true;
            LOGGER.debug("[{}] profile country updated", requestId);
        }

        if (isUpdateParamValid(request.getZipcode(), profile.getZipcode())) {
            profile.setZipcode(request.getZipcode().trim());
            isProfileDataUpdated = true;
            LOGGER.debug("[{}] profile zipcode updated", requestId);
        }

        if (isUpdateParamValid(request.getBusinessDetails(), profile.getBusinessDetails())) {
            profile.setBusinessDetails(request.getBusinessDetails().trim());
            isProfileDataUpdated = true;
            LOGGER.debug("[{}] profile business details updated", requestId);
        }

        if (isProfileDataUpdated) {
            final boolean profileUpdated = databaseService.updateProfile(profile);

            if (!profileUpdated) {
                LOGGER.error("[{}] profile update failed", requestId);
                return UpdateResponse.internalServerError("Error updating profile. Try again later");
            }

            LOGGER.info("[{}] profile updated successfully for id={}", requestId, profileId);
            return UpdateResponse.ok("Profile successfully updated");
        }

        return UpdateResponse.ok("Profile unchanged because no updates were made");
    }

    private boolean isValidProfilePicture(final MultipartFile profilePicture) {
        if (profilePicture == null) return false;
        final String contentType = profilePicture.getContentType();
        return contentType != null && contentType.startsWith("image");
    }

    private void checkUpdateProfileRequestForAllNullValues(final UpdateProfileRequest request) {
        final Class<UpdateProfileRequest> requestClass = UpdateProfileRequest.class;
        final Field[] fields = requestClass.getDeclaredFields();

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

    public UpdateResponse updatePassword(
            @Nonnull final HttpServletRequest httpRequest,
            @Nonnull final String profileId,
            @Nonnull final UpdatePasswordRequest updatePasswordRequest
    ) {
        checkProfileId(profileId);

        final var requestId = extractRequestIdFromRequest(httpRequest);

        LOGGER.info("[{}] update password request: {}", requestId, profileId);

        if (!arePasswordsEqual(updatePasswordRequest)) {
            LOGGER.warn("[{}] New password and confirm password mismatched", requestId);
            return UpdateResponse.badRequest("New password and confirm password mismatched");
        }

        final Profile profile = getProfileById(requestId, profileId);

        if (!matchPassword(updatePasswordRequest.currentPassword(), profile.getPassword())) {
            LOGGER.warn("[{}] incorrect password provide", requestId);
            return UpdateResponse.badRequest("Incorrect current password");
        }

        final var encryptedPassword = encryptPassword(updatePasswordRequest.newPassword());

        final boolean isPasswordUpdated = databaseService.updatePassword(profileId, encryptedPassword);

        if (!isPasswordUpdated) {
            LOGGER.error("[{}] password update failed", requestId);
            return UpdateResponse.internalServerError("Error updating password. Please try again");
        }

        LOGGER.info("[{}] password updated successfully", requestId);
        notificationService.sendPasswordChangedSuccessEmail(profile.getEmail());
        return UpdateResponse.ok("Password updated successfully");
    }

    private Profile getProfileById(
            @Nonnull final Object requestId,
            @Nonnull final String profileId
    ) {
        return databaseService.findByProfileId(profileId)
                .orElseThrow(() -> {
                    LOGGER.warn("[{}] profile not found with id: {}", requestId, profileId);
                    return new ResourceNotFoundException("Profile not found with id: " + profileId);
                });
    }

    private boolean arePasswordsEqual(@Nonnull final UpdatePasswordRequest request) {
        return Objects.equal(request.newPassword(), request.confirmPassword());
    }

    private String encryptPassword(@Nonnull final String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private boolean matchPassword(
            @Nonnull final String rawPassword,
            @Nonnull final String hashedPassword
    ) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    private void checkProfileId(final String profileId) {
        if (profileId == null || profileId.isBlank()) {
            throw new BadRequestException(new String[]{"Invalid profile id provided: " + profileId}, "Bad Request");
        }
    }

    private boolean isUpdateParamValid(final String reqParam, final String profileParam) {
        return reqParam != null && !Objects.equal(reqParam, profileParam);
    }

}

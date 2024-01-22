package com.akgarg.profile.profile.v1;

import com.akgarg.profile.exception.ResourceNotFoundException;
import com.akgarg.profile.notification.NotificationService;
import com.akgarg.profile.profile.v1.db.DatabaseService;
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

import java.util.Optional;

import static com.akgarg.profile.utils.ProfileUtils.extractRequestIdFromRequest;

@Service
public class ProfileService {

    private static final Logger LOGGER = LogManager.getLogger(ProfileService.class);
    private static final int PASSWORD_ENCODER_STRENGTH = 10;

    private final DatabaseService databaseService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(@Nonnull final DatabaseService databaseService,
                          @Nonnull final NotificationService notificationService) {
        this.databaseService = databaseService;
        this.notificationService = notificationService;
        this.passwordEncoder = new BCryptPasswordEncoder(PASSWORD_ENCODER_STRENGTH);
    }

    public Optional<ProfileDTO> getProfileByProfileId(@Nonnull final HttpServletRequest httpRequest,
                                                      @Nonnull final String userId) {
        final var requestId = extractRequestIdFromRequest(httpRequest);

        LOGGER.info("[{}] get profile by profile id: {}", requestId, userId);

        final Optional<Profile> profile = databaseService.findByProfileId(userId);

        if (profile.isEmpty()) {
            LOGGER.error("[{}] profile not found by id: {}", requestId, userId);
            return Optional.empty();
        }

        return Optional.of(ProfileDTO.fromProfile(profile.get()));
    }

    public DeleteResponse deleteProfile(@Nonnull final HttpServletRequest httpRequest,
                                        @Nonnull final String profileId) {
        final var requestId = extractRequestIdFromRequest(httpRequest);

        LOGGER.info("[{}] get profile by user id: {}", requestId, profileId);

        final boolean profileDeleted = databaseService.deleteProfileById(profileId);

        if (profileDeleted) {
            return new DeleteResponse(HttpStatus.OK.value(), "Profile deleted successfully");
        }

        return new DeleteResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error deleting profile by id: " + profileId);
    }

    public UpdateResponse updateProfile(
            @Nonnull final HttpServletRequest httpRequest,
            @Nonnull final String profileId,
            @Nonnull final UpdateProfileRequest request
    ) {
        final var requestId = extractRequestIdFromRequest(httpRequest);

        LOGGER.info("[{}] update profile request for id={}: {}", requestId, profileId, request);

        final Profile profile = getProfileById(requestId, profileId);

        if (request.name() != null) {
            profile.setName(request.name());
        }

        if (request.bio() != null) {
            profile.setBio(request.bio().trim());
        }

        if (request.phone() != null) {
            profile.setPhone(request.phone());
        }

        if (request.city() != null) {
            profile.setCity(request.city());
        }

        if (request.state() != null) {
            profile.setState(request.state());
        }

        if (request.country() != null) {
            profile.setCountry(request.country());
        }

        if (request.zipcode() != null) {
            profile.setZipcode(request.zipcode());
        }

        if (request.businessDetails() != null) {
            profile.setBusinessDetails(request.businessDetails());
        }

        final boolean profileUpdated = databaseService.updateProfile(profile);

        if (!profileUpdated) {
            LOGGER.error("[{}] profile update failed", requestId);
            return UpdateResponse.internalServerError("Error updating profile. Try again later");
        }

        return UpdateResponse.ok("Profile successfully updated");
    }

    public UpdateResponse updatePassword(@Nonnull final HttpServletRequest httpRequest,
                                         @Nonnull final String profileId,
                                         @Nonnull final UpdatePasswordRequest updatePasswordRequest) {
        final var requestId = extractRequestIdFromRequest(httpRequest);

        LOGGER.info("[{}] update password request: {}", requestId, profileId);

        if (!arePasswordsEqual(updatePasswordRequest)) {
            LOGGER.warn("[{}] New Passwords mismatched", requestId);
            return UpdateResponse.badRequest("New password and confirm password mismatched");
        }

        final Profile profile = getProfileById(requestId, profileId);

        if (!matchPassword(profile.getPassword(), updatePasswordRequest.currentPassword())) {
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
        notificationService.sendPasswordChangedSuccessEmail();
        return UpdateResponse.ok("Password updated successfully");
    }

    private Profile getProfileById(@Nonnull final Object requestId,
                                   @Nonnull final String profileId) {
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

    private boolean matchPassword(@Nonnull final String rawPassword,
                                  @Nonnull final String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

}

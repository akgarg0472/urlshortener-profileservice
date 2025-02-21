package com.akgarg.profile.profile.v1;

import com.akgarg.profile.exception.BadRequestException;
import com.akgarg.profile.request.UpdatePasswordRequest;
import com.akgarg.profile.request.UpdateProfileRequest;
import com.akgarg.profile.response.DeleteResponse;
import com.akgarg.profile.response.ProfileResponse;
import com.akgarg.profile.response.UpdateResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.akgarg.profile.utils.ProfileUtils.USER_ID_HEADER_NAME;
import static com.akgarg.profile.utils.ProfileUtils.checkValidationResultAndThrowExceptionOnFailure;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Endpoints for managing user profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Fetch user profile", description = "Fetch the profile of a user by the unique profileId.")
    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileResponse> getProfile(
            @RequestHeader(USER_ID_HEADER_NAME) final String userIdHeader,
            @PathVariable("profileId") @Parameter(description = "Unique identifier for the user profile.") final String profileId
    ) {
        validateProfileIds(userIdHeader, profileId);
        final var profile = profileService.getProfileByProfileId(profileId);
        return profile
                .map(p -> ResponseEntity.ok(new ProfileResponse(HttpStatus.OK.value(), "Profile found successfully", p)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ProfileResponse.notFound("Profile not found with id: " + profileId)));
    }

    @Operation(summary = "Update user profile", description = "Update the details of a user profile. You can also update the profile picture.")
    @PatchMapping(value = "/{profileId}")
    public ResponseEntity<UpdateResponse> updateProfile(
            @RequestHeader(USER_ID_HEADER_NAME) final String userIdHeader,
            @PathVariable("profileId") @Parameter(description = "Unique identifier for the user profile.") final String profileId,
            @RequestParam(value = "profile_picture", required = false) @Parameter(description = "New profile picture file.") final MultipartFile profilePicture,
            @RequestParam("req_body") @Parameter(description = "Request body containing the fields to update (e.g., name, email).") final String requestBody
    ) throws JsonProcessingException {
        validateProfileIds(userIdHeader, profileId);
        final var updateRequest = objectMapper.readValue(requestBody, UpdateProfileRequest.class);
        updateRequest.setProfilePicture(profilePicture);
        final var updateResponse = profileService.updateProfile(profileId, updateRequest);
        return ResponseEntity.status(updateResponse.statusCode()).body(updateResponse);
    }

    @Operation(summary = "Update user password", description = "Update the password for the user profile.")
    @PatchMapping("/{profileId}/password")
    public ResponseEntity<UpdateResponse> updatePassword(
            @RequestHeader(USER_ID_HEADER_NAME) final String userIdHeader,
            @PathVariable("profileId") @Parameter(description = "Unique identifier for the user profile.") final String profileId,
            @RequestBody @Valid @Parameter(description = "Request body containing the old and new password fields.") final UpdatePasswordRequest updatePasswordRequest,
            final BindingResult validationResult
    ) {
        validateProfileIds(userIdHeader, profileId);
        checkValidationResultAndThrowExceptionOnFailure(validationResult);
        final var updateResponse = profileService.updatePassword(profileId, updatePasswordRequest);
        return ResponseEntity.status(updateResponse.statusCode()).body(updateResponse);
    }

    @Operation(summary = "Delete user profile", description = "Delete a user profile using the unique profileId.")
    @DeleteMapping("/{profileId}")
    public ResponseEntity<DeleteResponse> deleteProfile(
            @RequestHeader(USER_ID_HEADER_NAME) final String userIdHeader,
            @PathVariable("profileId") @Parameter(description = "Unique identifier for the user profile.") final String profileId
    ) {
        validateProfileIds(userIdHeader, profileId);
        final var deleteResponse = profileService.deleteProfile(profileId);
        return ResponseEntity.status(deleteResponse.statusCode()).body(deleteResponse);
    }

    private void validateProfileIds(final String userIdHeader, final String profileId) {
        if (userIdHeader == null || !userIdHeader.equals(profileId)) {
            throw new BadRequestException(new String[]{"UserId in header %s is different from the received id".formatted(USER_ID_HEADER_NAME)}, "Invalid profile IDs provided");
        }
    }

}
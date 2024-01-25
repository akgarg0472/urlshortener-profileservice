package com.akgarg.profile.profile.v1;

import com.akgarg.profile.request.UpdatePasswordRequest;
import com.akgarg.profile.request.UpdateProfileRequest;
import com.akgarg.profile.response.DeleteResponse;
import com.akgarg.profile.response.ProfileResponse;
import com.akgarg.profile.response.UpdateResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.akgarg.profile.utils.ProfileUtils.checkValidationResultAndThrowExceptionOnFailure;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final ObjectMapper objectMapper;

    public ProfileController(
            final ProfileService profileService,
            final ObjectMapper objectMapper
    ) {
        this.profileService = profileService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileResponse> getProfile(
            final HttpServletRequest httpRequest, @PathVariable("profileId") final String profileId
    ) {
        final Optional<ProfileDTO> profile = profileService.getProfileByProfileId(httpRequest, profileId);
        return profile.map(p -> ResponseEntity.ok(new ProfileResponse(HttpStatus.OK.value(), "Profile found successfully", p))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ProfileResponse.notFound("Profile not found with id: " + profileId)));
    }

    @PatchMapping(value = "/{profileId}")
    public ResponseEntity<UpdateResponse> updateProfile(
            final HttpServletRequest httpRequest,
            @PathVariable("profileId") final String profileId,
            @RequestParam(value = "profile_picture", required = false) final MultipartFile profilePicture,
            @RequestParam("req_body") final String requestBody
    ) throws JsonProcessingException {
        final var updateRequest = objectMapper.readValue(requestBody, UpdateProfileRequest.class);
        updateRequest.setProfilePicture(profilePicture);
        final UpdateResponse updateResponse = profileService.updateProfile(httpRequest, profileId, updateRequest);
        return ResponseEntity.status(updateResponse.statusCode()).body(updateResponse);
    }

    @PatchMapping("/{profileId}/password")
    public ResponseEntity<UpdateResponse> updatePassword(
            final HttpServletRequest httpRequest,
            @PathVariable("profileId") final String profileId,
            @RequestBody @Valid final UpdatePasswordRequest updatePasswordRequest,
            final BindingResult validationResult
    ) {
        checkValidationResultAndThrowExceptionOnFailure(validationResult);
        final UpdateResponse updateResponse = profileService.updatePassword(httpRequest, profileId, updatePasswordRequest);
        return ResponseEntity.status(updateResponse.statusCode()).body(updateResponse);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<DeleteResponse> deleteProfile(
            final HttpServletRequest httpRequest, @PathVariable("profileId") final String profileId
    ) {
        final DeleteResponse deleteResponse = profileService.deleteProfile(httpRequest, profileId);
        return ResponseEntity.status(deleteResponse.statusCode()).body(deleteResponse);
    }

}

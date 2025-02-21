package com.akgarg.profile.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.akgarg.profile.utils.ProfileUtils.maskString;

public record UpdatePasswordRequest(
        @JsonProperty("current_password")
        @NotBlank(message = "current_password is required")
        @Size(min = 8, message = "current_password must be at least 8 characters long")
        String currentPassword,

        @JsonProperty("new_password")
        @NotBlank(message = "new_password is required")
        @Size(min = 8, message = "new_password must be at least 8 characters long")
        String newPassword,

        @JsonProperty("confirm_password")
        @NotBlank(message = "confirm_password is required")
        @Size(min = 8, message = "confirm_password must be at least 8 characters long")
        String confirmPassword) {

    @Override
    public String toString() {
        return "{" +
                "currentPassword='" + maskString(currentPassword) + '\'' +
                ", newPassword='" + maskString(newPassword) + '\'' +
                ", confirmPassword='" + maskString(confirmPassword) + '\'' +
                '}';
    }

}

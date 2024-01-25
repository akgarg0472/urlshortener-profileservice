package com.akgarg.profile.profile.v1;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfileDTO(
        @JsonProperty("id") String id,
        @JsonProperty("username") String username,
        @JsonProperty("name") String name,
        @JsonProperty("bio") String bio,
        @JsonProperty("profile_picture") String profilePictureUrl,
        @JsonProperty("phone") String phone,
        @JsonProperty("city") String city,
        @JsonProperty("country") String country,
        @JsonProperty("last_login") long lastLoginAt,
        @JsonProperty("last_password_changed") long lastPasswordChangedAt,
        @JsonProperty("premium_account") boolean premiumAccount,
        @JsonProperty("created_at") long createdAt,
        @JsonProperty("updated_at") long updatedAt
) {

    public static ProfileDTO notFound() {
        return new ProfileDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                -1,
                -1,
                false,
                -1,
                -1
        );
    }

    public static ProfileDTO fromProfile(final Profile profile) {
        return new ProfileDTO(
                profile.getId(),
                profile.getUsername(),
                profile.getName(),
                profile.getBio(),
                notFound().profilePictureUrl(),
                profile.getPhone(),
                profile.getCity(),
                profile.getCountry(),
                profile.getLastLoginAt(),
                profile.getLastPasswordChangedAt(),
                profile.isPremiumAccount(),
                profile.getCreatedAt().getTime(),
                profile.getUpdatedAt().getTime()
        );
    }

}

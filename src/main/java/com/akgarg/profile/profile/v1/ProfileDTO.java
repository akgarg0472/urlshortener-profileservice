package com.akgarg.profile.profile.v1;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfileDTO(
        @JsonProperty("id") String id,
        @JsonProperty("email") String email,
        @JsonProperty("name") String name,
        @JsonProperty("bio") String bio,
        @JsonProperty("profile_picture") String profilePictureUrl,
        @JsonProperty("phone") String phone,
        @JsonProperty("city") String city,
        @JsonProperty("state") String state,
        @JsonProperty("country") String country,
        @JsonProperty("zipcode") String zipcode,
        @JsonProperty("last_login") long lastLoginAt,
        @JsonProperty("last_password_changed") Long lastPasswordChangedAt,
        @JsonProperty("premium_account") boolean premiumAccount,
        @JsonProperty("business_details") String businessDetails,
        @JsonProperty("created_at") long createdAt,
        @JsonProperty("updated_at") Long updatedAt
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
                null,
                null,
                -1,
                -1L,
                false,
                null,
                -1,
                -1L
        );
    }

    public static ProfileDTO fromProfile(final Profile profile) {
        return new ProfileDTO(
                profile.getId(),
                profile.getEmail(),
                profile.getName(),
                profile.getBio(),
                profile.getProfilePictureUrl(),
                profile.getPhone(),
                profile.getCity(),
                profile.getState(),
                profile.getCountry(),
                profile.getZipcode(),
                profile.getLastLoginAt(),
                profile.getLastPasswordChangedAt(),
                profile.isPremiumAccount(),
                profile.getBusinessDetails(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

}

package com.akgarg.profile.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateProfileRequest(
        @JsonProperty("name") String name,
        @JsonProperty("bio") String bio,
        @JsonProperty("phone") String phone,
        @JsonProperty("city") String city,
        @JsonProperty("state") String state,
        @JsonProperty("country") String country,
        @JsonProperty("zipcode") String zipcode,
        @JsonProperty("business_details") String businessDetails
) {
}

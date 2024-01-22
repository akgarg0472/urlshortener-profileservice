package com.akgarg.profile.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfileResponse(
        @JsonProperty("status_code") int statusCode,
        @JsonProperty("message") String message,
        @JsonProperty("data") Object data
) {

    public static ProfileResponse notFound(final String message) {
        return new ProfileResponse(404, message, null);
    }

}
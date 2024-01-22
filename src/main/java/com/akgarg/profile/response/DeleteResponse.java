package com.akgarg.profile.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteResponse(
        @JsonProperty("status_code") int statusCode,
        @JsonProperty("message") String message
) {
}

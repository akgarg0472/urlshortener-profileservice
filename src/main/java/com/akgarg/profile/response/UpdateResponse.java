package com.akgarg.profile.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

public record UpdateResponse(
        @JsonProperty("status_code") int statusCode,
        @JsonProperty("message") String message,
        @JsonProperty("errors") Object errors
) {

    public static UpdateResponse notFound(final Object... errors) {
        return new UpdateResponse(HttpStatus.NOT_FOUND.value(), "Not Found", errors);
    }

    public static UpdateResponse badRequest(final Object... errors) {
        return new UpdateResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", errors);
    }

    public static UpdateResponse internalServerError(final Object... errors) {
        return new UpdateResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", errors);
    }

    public static UpdateResponse ok(final String message) {
        return new UpdateResponse(HttpStatus.OK.value(), message, null);
    }

}

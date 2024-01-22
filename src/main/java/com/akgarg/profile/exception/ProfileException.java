package com.akgarg.profile.exception;

import lombok.Getter;

@Getter
public class ProfileException extends RuntimeException {

    private final String[] errors;
    private final int errorCode;
    private final String message;

    public ProfileException(final String[] errors, final int errorCode, String message) {
        this.errors = errors;
        this.errorCode = errorCode;
        this.message = message;
    }

}

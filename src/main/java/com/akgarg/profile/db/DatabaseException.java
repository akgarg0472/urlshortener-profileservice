package com.akgarg.profile.db;

public class DatabaseException extends RuntimeException {

    private final int code;

    public DatabaseException(final String message, final int code) {
        super(message);
        this.code = code;
    }

    public int code() {
        return code;
    }

}

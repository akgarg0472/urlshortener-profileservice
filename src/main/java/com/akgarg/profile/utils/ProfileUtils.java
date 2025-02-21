package com.akgarg.profile.utils;

import com.akgarg.profile.exception.BadRequestException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.Random;

public final class ProfileUtils {

    public static final String USER_ID_HEADER_NAME = "X-USER-ID";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String REQUEST_ID_THREAD_CONTEXT_KEY = "requestId";

    private static final Random random = new Random();

    private ProfileUtils() {
        throw new IllegalStateException();
    }

    public static void checkValidationResultAndThrowExceptionOnFailure(
            final BindingResult validationResult
    ) {
        if (validationResult.hasFieldErrors()) {
            final String[] errors = validationResult.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList()
                    .toArray(String[]::new);
            throw new BadRequestException(errors, "Request Validation Failed");
        }
    }

    public static String maskString(final String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        final var length = input.length();
        final var minMaskCount = (int) Math.ceil(length * 0.4);
        final var maskedArray = input.toCharArray();
        final var maskedIndices = new boolean[length];
        int maskedCount = 0;

        while (maskedCount < minMaskCount) {
            var index = random.nextInt(length);
            if (!maskedIndices[index]) {
                maskedArray[index] = '*';
                maskedIndices[index] = true;
                maskedCount++;
            }
        }

        return new String(maskedArray);
    }

}

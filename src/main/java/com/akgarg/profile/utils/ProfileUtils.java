package com.akgarg.profile.utils;

import com.akgarg.profile.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

public final class ProfileUtils {

    private ProfileUtils() {
        throw new IllegalStateException();
    }

    public static Object extractRequestIdFromRequest(final HttpServletRequest httpRequest) {
        final var requestId = httpRequest.getAttribute("requestId");
        return requestId != null ? requestId : System.nanoTime();
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

}

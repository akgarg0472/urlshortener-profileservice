package com.akgarg.profile.encoder;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderWithBCryptTest {

    @Test
    void test_BcryptEncoding_WithDifferentHashingRoundsNumber() {
        final var passwordEncoderWithStrengthTen = new BCryptPasswordEncoder(10);
        final var passwordEncoderWithStrengthFifteen = new BCryptPasswordEncoder(15);

        assertNotNull(passwordEncoderWithStrengthTen, "Password encoder (10 strength) can't be null");
        assertNotNull(passwordEncoderWithStrengthFifteen, "Password encoder (15 strength) can't be null");

        final var rawPassword = "Pass@1234#";

        final var encryptedPasswordUsingStrengthTen = passwordEncoderWithStrengthTen.encode(rawPassword);
        final var encryptedPasswordUsingStrengthFifteen = passwordEncoderWithStrengthFifteen.encode(rawPassword);

        assertNotEquals(encryptedPasswordUsingStrengthTen, encryptedPasswordUsingStrengthFifteen, "Passwords generated using different strengths can't be same");

        final var strengthTenMatchingPasswordGeneratedUsingStrengthFifteen = passwordEncoderWithStrengthTen.matches(rawPassword, encryptedPasswordUsingStrengthFifteen);
        final var strengthFifteenMatchingPasswordGeneratedUsingStrengthTen = passwordEncoderWithStrengthFifteen.matches(rawPassword, encryptedPasswordUsingStrengthTen);

        assertTrue(strengthTenMatchingPasswordGeneratedUsingStrengthFifteen, "Password encoder with strength 10 is unable to match the password hashed using strength 15 password encoder");
        assertTrue(strengthFifteenMatchingPasswordGeneratedUsingStrengthTen, "Password encoder with strength 15 is unable to match the password hashed using strength 10 password encoder");
    }

}

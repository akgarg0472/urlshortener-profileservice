package com.akgarg.profile.encoder;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class TestPasswordEncoderWithBCrypt {

    @Test
    void test_BcryptEncoding_WithDifferentHashingRoundsNumber() {
        final PasswordEncoder passwordEncoderWithStrengthTen = new BCryptPasswordEncoder(10);
        final PasswordEncoder passwordEncoderWithStrengthFifteen = new BCryptPasswordEncoder(15);

        assertNotNull(passwordEncoderWithStrengthTen, "Password encoder (10 strength) can't be null");
        assertNotNull(passwordEncoderWithStrengthFifteen, "Password encoder (15 strength) can't be null");

        final String rawPassword = "Pass@1234#";

        final String encryptedPasswordUsingStrengthTen = passwordEncoderWithStrengthTen.encode(rawPassword);
        final String encryptedPasswordUsingStrengthFifteen = passwordEncoderWithStrengthFifteen.encode(rawPassword);

        assertNotEquals(encryptedPasswordUsingStrengthTen, encryptedPasswordUsingStrengthFifteen, "Passwords generated using different strengths can't be same");

        final boolean strengthTenMatchingPasswordGeneratedUsingStrengthFifteen = passwordEncoderWithStrengthTen.matches(rawPassword, encryptedPasswordUsingStrengthFifteen);
        final boolean strengthFifteenMatchingPasswordGeneratedUsingStrengthTen = passwordEncoderWithStrengthFifteen.matches(rawPassword, encryptedPasswordUsingStrengthTen);

        assertTrue(strengthTenMatchingPasswordGeneratedUsingStrengthFifteen, "Password encoder with strength 10 is unable to match the password hashed using strength 15 password encoder");
        assertTrue(strengthFifteenMatchingPasswordGeneratedUsingStrengthTen, "Password encoder with strength 15 is unable to match the password hashed using strength 10 password encoder");
    }

}

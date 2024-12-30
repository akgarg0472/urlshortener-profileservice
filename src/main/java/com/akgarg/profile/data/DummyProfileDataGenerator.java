package com.akgarg.profile.data;

import com.akgarg.profile.profile.v1.Profile;
import com.github.javafaker.Faker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class DummyProfileDataGenerator {

    private DummyProfileDataGenerator() {
        throw new IllegalStateException("utility class");
    }

    static Collection<Profile> generateDummyProfiles(final int numberOfProfiles) {
        final var faker = new Faker();
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        final List<Profile> profiles = new ArrayList<>();

        for (int i = 1; i <= numberOfProfiles; i++) {
            final var profile = new Profile(String.valueOf(i));
            profile.setEmail(faker.internet().emailAddress());
            profile.setPassword(passwordEncoder.encode(faker.internet().password()));
            profile.setScopes("user");
            profile.setName(faker.name().fullName());
            profile.setBio(faker.lorem().paragraph());
            profile.setPhone(faker.phoneNumber().phoneNumber());
            profile.setCity(faker.address().cityName());
            profile.setState(faker.address().state());
            profile.setCountry(faker.address().country());
            profile.setZipcode(faker.address().zipCode());
            profile.setPremiumAccount(faker.bool().bool());
            profile.setDeleted(false);
            final var date = faker.date().past(225 * 24, TimeUnit.HOURS);
            profile.setLastPasswordChangedAt(date.getTime());
            profile.setLastLoginAt(faker.date().past(1, TimeUnit.HOURS).getTime());
            profile.setCreatedAt(Timestamp.from(Instant.ofEpochMilli(date.getTime() - (90L * 24 * 60 * 60 * 1000))).getTime());
            profile.setUpdatedAt(Timestamp.from(Instant.ofEpochMilli(faker.date().past(41, TimeUnit.DAYS).getTime())).getTime());
            profiles.add(profile);
        }

        return profiles;
    }

}

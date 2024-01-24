package com.akgarg.profile.data;

import com.akgarg.profile.db.DatabaseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.akgarg.profile.data.DummyProfileDataGenerator.generateDummyProfiles;

@Component
@Profile("dev")
class DummyProfileDataInitializer implements CommandLineRunner {

    private final DatabaseService databaseService;

    DummyProfileDataInitializer(final DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void run(final String... args) {
        generateDummyProfiles(10)
                .forEach(databaseService::addProfile);
    }

}

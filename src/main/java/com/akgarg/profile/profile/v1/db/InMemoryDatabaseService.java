package com.akgarg.profile.profile.v1.db;

import com.akgarg.profile.profile.v1.Profile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDatabaseService implements DatabaseService {

    private static final Logger LOGGER = LogManager.getLogger(InMemoryDatabaseService.class);

    private final Map<String, Profile> db;

    public InMemoryDatabaseService() {
        this.db = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<Profile> findByProfileId(String profileId) {
        return Optional.empty();
    }

    @Override
    public Optional<Profile> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public boolean updateProfile(Profile profile) {
        return false;
    }

    @Override
    public boolean deleteProfileById(String profileId) {
        return false;
    }

    @Override
    public boolean updatePassword(String profileId, String encryptedPassword) {
        return false;
    }

}

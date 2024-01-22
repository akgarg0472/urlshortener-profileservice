package com.akgarg.profile.profile.v1.db;

import com.akgarg.profile.profile.v1.Profile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDatabaseService implements DatabaseService {

    private static final Logger LOGGER = LogManager.getLogger(InMemoryDatabaseService.class);

    private static final String PROFILE_ID_CANT_NULL = "Profile id can't be null";

    private final Map<String, Profile> db;

    public InMemoryDatabaseService() {
        this.db = new ConcurrentHashMap<>();
    }

    @Override
    public boolean addProfile(final Profile profile) {
        Objects.requireNonNull(profile, "Profile can't be null");
        LOGGER.info("Adding profile to DB: {}", profile.getId());
        return db.putIfAbsent(profile.getId(), profile) == null;
    }

    @Override
    public Optional<Profile> findByProfileId(final String profileId) {
        Objects.requireNonNull(profileId, PROFILE_ID_CANT_NULL);
        return Optional.ofNullable(db.getOrDefault(profileId, null));
    }

    @Override
    public boolean updateProfile(final Profile profile) {
        Objects.requireNonNull(profile, "Profile can't be null");

        if (!db.containsKey(profile.getId())) {
            LOGGER.warn("No profile found with id: {}", profile.getId());
            return false;
        }

        return db.put(profile.getId(), profile) != null;
    }

    @Override
    public boolean deleteProfileById(final String profileId) {
        Objects.requireNonNull(profileId, PROFILE_ID_CANT_NULL);
        final boolean profileDeleted = db.remove(profileId) != null;
        LOGGER.info("Profile with id={} deleted: {}", profileId, profileDeleted);
        return profileDeleted;
    }

    @Override
    public boolean updatePassword(final String profileId, final String encryptedPassword) {
        Objects.requireNonNull(profileId, PROFILE_ID_CANT_NULL);
        Objects.requireNonNull(encryptedPassword, "Encrypted password can't be null");

        final Profile profile = db.get(profileId);

        if (profile == null) {
            LOGGER.warn("No profile found with id: {}", profileId);
            return false;
        }

        profile.setPassword(encryptedPassword);

        return db.put(profileId, profile) != null;
    }

    @Override
    public Collection<Profile> findAllProfiles() {
        return db.values();
    }

}

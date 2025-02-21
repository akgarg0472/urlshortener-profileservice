package com.akgarg.profile.db;

import com.akgarg.profile.profile.v1.Profile;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDatabaseService implements DatabaseService {

    private static final String PROFILE_ID_CANT_NULL = "Profile id can't be null";

    private final Map<String, Profile> db;

    public InMemoryDatabaseService() {
        this.db = new ConcurrentHashMap<>();
    }

    @Override
    public boolean addProfile(final Profile profile) {
        Objects.requireNonNull(profile, "Profile can't be null");
        return !isProfileExistsByEmail(profile.getEmail()) &&
                db.putIfAbsent(profile.getId(), profile) == null;
    }

    @Override
    public Optional<Profile> findByProfileId(final String profileId) {
        Objects.requireNonNull(profileId, PROFILE_ID_CANT_NULL);
        return Optional.ofNullable(db.getOrDefault(profileId, null));
    }

    @Override
    public void updateProfile(final Profile profile) {
        Objects.requireNonNull(profile, "Profile can't be null");

        if (!db.containsKey(profile.getId())) {
            return;
        }

        db.put(profile.getId(), profile);
    }

    @Override
    public void deleteProfileById(final String profileId) {
        Objects.requireNonNull(profileId, PROFILE_ID_CANT_NULL);
        db.remove(profileId);
    }

    @Override
    public void updatePassword(final String profileId, final String encryptedPassword) {
        Objects.requireNonNull(profileId, PROFILE_ID_CANT_NULL);
        Objects.requireNonNull(encryptedPassword, "Encrypted password can't be null");

        final Profile profile = db.get(profileId);

        if (profile == null) {
            return;
        }

        profile.setPassword(encryptedPassword);

        db.put(profileId, profile);
    }

    private boolean isProfileExistsByEmail(final String email) {
        return db.values()
                .stream()
                .anyMatch(profile -> Objects.equals(profile.getEmail(), email));
    }

}

package com.akgarg.profile.db;

import com.akgarg.profile.profile.v1.Profile;

import java.util.Collection;
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
    public boolean updateProfile(final Profile profile) {
        Objects.requireNonNull(profile, "Profile can't be null");

        if (!db.containsKey(profile.getId())) {
            return false;
        }

        return db.put(profile.getId(), profile) != null;
    }

    @Override
    public boolean deleteProfileById(final String profileId) {
        Objects.requireNonNull(profileId, PROFILE_ID_CANT_NULL);
        return db.remove(profileId) != null;
    }

    @Override
    public boolean updatePassword(final String profileId, final String encryptedPassword) {
        Objects.requireNonNull(profileId, PROFILE_ID_CANT_NULL);
        Objects.requireNonNull(encryptedPassword, "Encrypted password can't be null");

        final Profile profile = db.get(profileId);

        if (profile == null) {
            return false;
        }

        profile.setPassword(encryptedPassword);

        return db.put(profileId, profile) != null;
    }

    @Override
    public Collection<Profile> findAllProfiles() {
        return db.values();
    }

    private boolean isProfileExistsByEmail(final String email) {
        return db.values()
                .stream()
                .anyMatch(profile -> Objects.equals(profile.getEmail(), email));
    }

}

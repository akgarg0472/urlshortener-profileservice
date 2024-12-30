package com.akgarg.profile.db;

import com.akgarg.profile.db.repo.ProfileRepository;
import com.akgarg.profile.profile.v1.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class MysqlDatabaseService implements DatabaseService {

    private static final String PROFILE_ID_NULL_MSG = "Profile id can't be null";
    private static final String PROFILE_NOT_FOUND_BY_ID_MSG = "Profile not found by id: %s";

    private final ProfileRepository profileRepository;

    @Override
    public boolean addProfile(final Profile profile) {
        Objects.requireNonNull(profile, "Profile instance is null");

        try {
            if (profileRepository.existsByEmail(profile.getEmail())) {
                throw new DatabaseException("User with email '" + profile.getEmail() + "' already exists", 409);
            }
            profileRepository.save(profile);
            return true;
        } catch (final DataIntegrityViolationException e) {
            log.error("Error adding profile: ", e);
            throw new DatabaseException("Database constraint violation", 500);
        }
    }

    @Override
    public Optional<Profile> findByProfileId(final String profileId) {
        Objects.requireNonNull(profileId, PROFILE_ID_NULL_MSG);
        return profileRepository.findByIdAndDeletedFalse(profileId);
    }

    @Override
    public Collection<Profile> findAllProfiles() {
        throw new UnsupportedOperationException("Fetching all profiles is not supported by the MySQL database service");
    }

    @Override
    public boolean updateProfile(final Profile profile) {
        Objects.requireNonNull(profile, "Profile to update can't be null");

        final var existingProfile = profileRepository.findByIdAndDeletedFalse(profile.getId());

        if (existingProfile.isEmpty()) {
            throw new DatabaseException(PROFILE_NOT_FOUND_BY_ID_MSG.formatted(profile.getId()), 500);
        }

        try {
            profileRepository.save(profile);
            return true;
        } catch (DataIntegrityViolationException e) {
            log.error("Error updating profile: ", e);
            throw new DatabaseException("Database constraint violation", 500);
        }
    }

    @Override
    public boolean updatePassword(final String profileId, final String encryptedPassword) {
        final var profileOptional = profileRepository.findByIdAndDeletedFalse(profileId);

        if (profileOptional.isPresent()) {
            final var profile = profileOptional.get();
            profile.setPassword(encryptedPassword);
            profile.setLastPasswordChangedAt(System.currentTimeMillis());
            profileRepository.save(profile);
            return true;
        } else {
            throw new DatabaseException(PROFILE_NOT_FOUND_BY_ID_MSG.formatted(profileId), 500);
        }
    }

    @Override
    public boolean deleteProfileById(final String profileId) {
        final var profileOptional = profileRepository.findById(profileId);

        if (profileOptional.isPresent()) {
            final var profile = profileOptional.get();
            profile.setDeleted(true);
            profileRepository.save(profile);
            return true;
        } else {
            throw new DatabaseException(PROFILE_NOT_FOUND_BY_ID_MSG.formatted(profileId), 500);
        }
    }

}

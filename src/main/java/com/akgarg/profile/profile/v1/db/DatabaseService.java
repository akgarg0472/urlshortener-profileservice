package com.akgarg.profile.profile.v1.db;

import com.akgarg.profile.profile.v1.Profile;

import java.util.Optional;

public interface DatabaseService {

    Optional<Profile> findByProfileId(String profileId);

    Optional<Profile> findByEmail(String email);

    boolean updateProfile(Profile profile);

    boolean deleteProfileById(String profileId);

    boolean updatePassword(String profileId, String encryptedPassword);

}

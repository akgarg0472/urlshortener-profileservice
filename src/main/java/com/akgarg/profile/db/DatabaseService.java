package com.akgarg.profile.db;

import com.akgarg.profile.profile.v1.Profile;

import java.util.Optional;

public interface DatabaseService {

    boolean addProfile(Profile profile);

    Optional<Profile> findByProfileId(String profileId);

    void updateProfile(Profile profile);

    void updatePassword(String profileId, String encryptedPassword);

    void deleteProfileById(String profileId);

}

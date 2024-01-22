package com.akgarg.profile.profile.v1.db;

import com.akgarg.profile.profile.v1.Profile;

import java.util.Collection;
import java.util.Optional;

public interface DatabaseService {

    boolean addProfile(Profile profile);

    Optional<Profile> findByProfileId(String profileId);

    boolean updateProfile(Profile profile);

    boolean deleteProfileById(String profileId);

    boolean updatePassword(String profileId, String encryptedPassword);

    Collection<Profile> findAllProfiles();

}

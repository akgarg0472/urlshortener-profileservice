package com.akgarg.profile.db;

import com.akgarg.profile.profile.v1.Profile;

import java.util.Collection;
import java.util.Optional;

public interface DatabaseService {

    boolean addProfile(Profile profile);

    Optional<Profile> findByProfileId(String profileId);

    Collection<Profile> findAllProfiles();

    boolean updateProfile(Profile profile);

    boolean updatePassword(String profileId, String encryptedPassword);

    boolean deleteProfileById(String profileId);


}

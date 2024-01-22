package com.akgarg.profile.profile.v1.db;

import com.akgarg.profile.profile.v1.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.Optional;

public class MysqlDatabaseService implements DatabaseService {

    private final JdbcTemplate jdbcTemplate;

    public MysqlDatabaseService(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public boolean addProfile(final Profile profile) {
        return false;
    }

    @Override
    public Optional<Profile> findByProfileId(String profileId) {
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

    @Override
    public Collection<Profile> findAllProfiles() {
        throw new UnsupportedOperationException("MySQL database service doesn't support findAllProfiles");
    }

}

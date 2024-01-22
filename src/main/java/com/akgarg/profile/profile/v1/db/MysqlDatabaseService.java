package com.akgarg.profile.profile.v1.db;

import com.akgarg.profile.profile.v1.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

public class MysqlDatabaseService implements DatabaseService {

    private final JdbcTemplate jdbcTemplate;

    public MysqlDatabaseService(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

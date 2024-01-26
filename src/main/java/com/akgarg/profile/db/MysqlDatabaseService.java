package com.akgarg.profile.db;

import com.akgarg.profile.profile.v1.Profile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class MysqlDatabaseService implements DatabaseService {

    private static final String PROFILE_ID_NULL_MSG = "Profile id can't be null";
    private static final Logger LOGGER = LogManager.getLogger(MysqlDatabaseService.class);

    private final JdbcTemplate jdbcTemplate;

    public MysqlDatabaseService(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addProfile(final Profile profile) {
        Objects.requireNonNull(profile, "Profile instance is null");

        final String insertQuery = "INSERT INTO users (id, email, password, name, scopes) VALUES (?, ?, ?, ?, ?);";

        try {
            return jdbcTemplate.update(
                    insertQuery,
                    profile.getId(),
                    profile.getEmail(),
                    profile.getPassword(),
                    profile.getName(),
                    profile.getScopes()
            ) == 1;
        } catch (Exception e) {
            if (isDuplicateInsertionException(e)) {
                throw new DatabaseException(getDuplicateInsertionErrorMessage(e), 409);
            }

            LOGGER.error("Error executing insert query", e);
            return false;
        }
    }

    @Override
    public Optional<Profile> findByProfileId(final String profileId) {
        Objects.requireNonNull(profileId, PROFILE_ID_NULL_MSG);

        final String findByIdQuery = "SELECT * FROM users WHERE id = ?";

        try {
            final List<Profile> profile = jdbcTemplate.query(
                    findByIdQuery,
                    ps -> ps.setString(1, profileId),
                    new ProfileRowMapper()
            );

            if (profile.size() == 1) {
                return Optional.of(profile.getFirst());
            }

            return Optional.empty();

        } catch (Exception e) {
            LOGGER.error("Error executing find by id query", e);
        }

        return Optional.empty();
    }

    @Override
    public Collection<Profile> findAllProfiles() {
        throw new UnsupportedOperationException("Fetching all profiles is not supported by the MySQL database service");
    }

    @Override
    public boolean updateProfile(final Profile profile) {
        Objects.requireNonNull(profile, "Profile to update can't be null");

        final String updateProfileQuery = """
                UPDATE users
                SET
                name = ?,
                bio = ?,
                profile_picture_url = ?,
                phone = ?,
                city = ?,
                state = ?,
                country = ?,
                zipcode = ?,
                business_details = ?
                WHERE id = ?;
                """;

        try {
            return jdbcTemplate.update(
                    updateProfileQuery,
                    profile.getName(),
                    profile.getBio(),
                    profile.getProfilePictureUrl(),
                    profile.getPhone(),
                    profile.getCity(),
                    profile.getState(),
                    profile.getCountry(),
                    profile.getZipcode(),
                    profile.getBusinessDetails(),
                    profile.getId()
            ) == 1;
        } catch (Exception e) {
            LOGGER.error("Error executing update password query", e);
        }

        return false;
    }

    @Override
    public boolean updatePassword(final String profileId, final String encryptedPassword) {
        Objects.requireNonNull(profileId, PROFILE_ID_NULL_MSG);
        Objects.requireNonNull(encryptedPassword, "Encrypted password can't be null");

        final String updatePasswordQuery = "UPDATE users SET password = ?, last_password_changed_at = ? WHERE id = ?";

        try {
            final long passwordChangeTimestamp = System.currentTimeMillis();
            return jdbcTemplate.update(updatePasswordQuery, encryptedPassword, passwordChangeTimestamp, profileId) == 1;
        } catch (Exception e) {
            LOGGER.error("Error executing update password query", e);
        }

        return false;
    }

    @Override
    public boolean deleteProfileById(final String profileId) {
        Objects.requireNonNull(profileId, PROFILE_ID_NULL_MSG);

        final String deleteProfileQuery = "UPDATE users SET is_deleted = true WHERE id = ?";

        try {
            return jdbcTemplate.update(deleteProfileQuery, profileId) == 1;
        } catch (Exception e) {
            LOGGER.error("Error executing delete profile query", e);
        }

        return false;
    }

    private boolean isDuplicateInsertionException(final Exception e) {
        return e instanceof DuplicateKeyException dke && dke.getCause() instanceof SQLIntegrityConstraintViolationException;
    }

    private String getDuplicateInsertionErrorMessage(final Exception e) {
        final String message = e.getCause().getMessage();
        final String[] keyValue = getDuplicateKeyValue(message);
        return "User with %s '%s' already exists".formatted(keyValue[1], keyValue[0]);
    }

    private String[] getDuplicateKeyValue(final String message) {
        final Pattern pattern = Pattern.compile("'(.*?)'");
        final Matcher matcher = pattern.matcher(message);
        final String[] keyValue = new String[2];

        int matchCount = 0;
        while (matcher.find() && matchCount < 2) {
            keyValue[matchCount++] = matcher.group(1);
        }

        if (keyValue[1] != null) {
            if (keyValue[1].endsWith("PRIMARY")) {
                keyValue[1] = "id";
            } else {
                final int lastDotIndex = keyValue[1].lastIndexOf(".");
                if (lastDotIndex != -1) {
                    keyValue[1] = keyValue[1].substring(lastDotIndex + 1);
                }
            }
        }

        return keyValue;
    }

    private static class ProfileRowMapper implements RowMapper<Profile> {
        @Override
        public Profile mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
            final Profile profile = new Profile();
            profile.setId(resultSet.getString("id"));
            profile.setProfilePictureUrl(resultSet.getString("profile_picture_url"));
            profile.setEmail(resultSet.getString("email"));
            profile.setPassword(resultSet.getString("password"));
            profile.setScopes(resultSet.getString("scopes"));
            profile.setName(resultSet.getString("name"));
            profile.setBio(resultSet.getString("bio"));
            profile.setPhone(resultSet.getString("phone"));
            profile.setCity(resultSet.getString("city"));
            profile.setState(resultSet.getString("state"));
            profile.setCountry(resultSet.getString("country"));
            profile.setZipcode(resultSet.getString("zipcode"));
            profile.setBusinessDetails(resultSet.getString("business_details"));
            profile.setPremiumAccount(resultSet.getBoolean("premium_account"));
            profile.setDeleted(resultSet.getBoolean("is_deleted"));
            profile.setLastPasswordChangedAt(resultSet.getLong("last_password_changed_at"));
            profile.setLastLoginAt(resultSet.getLong("last_login_at"));
            profile.setCreatedAt(resultSet.getTimestamp("created_at"));
            profile.setUpdatedAt(resultSet.getTimestamp("updated_at"));
            return profile;
        }
    }

}

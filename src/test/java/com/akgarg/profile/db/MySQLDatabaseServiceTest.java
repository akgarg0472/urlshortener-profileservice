package com.akgarg.profile.db;

import com.akgarg.profile.profile.v1.Profile;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static com.akgarg.profile.db.DatabaseTestUtil.getProfileObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlDialectInspection"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MySQLDatabaseServiceTest {

    private JdbcTemplate jdbcTemplate;
    private MysqlDatabaseService databaseService;

    @BeforeAll
    void init() {
        jdbcTemplate = DatabaseTestUtil.getJdbcTemplate();
        databaseService = new MysqlDatabaseService(jdbcTemplate);
    }

    @AfterAll
    void tearDown() {
        assertNotNull(jdbcTemplate, "JdbcTemplate is null");
        jdbcTemplate.execute("DROP DATABASE IF EXISTS urlshortener_users_test");
    }

    @BeforeEach
    void truncateUsersTable() {
        assertNotNull(jdbcTemplate, "JdbcTemplate is null");
        assertNotNull(databaseService, "Database service is null");
        jdbcTemplate.execute("TRUNCATE TABLE users");
    }

    @Test
    void addProfile_ShouldReturnTrue_WhenInsertingNewUniqueProfile() {
        assertTrue(databaseService.addProfile(getProfileObject("1")), "Database service should add profile info into DB");
    }

    @Test
    void addProfile_ShouldThrowDatabaseExceptionWithExactMessageAndCode_WhenInsertingDuplicateProfileWithSameId() {
        final var profileOne = getProfileObject("1");
        assertTrue(databaseService.addProfile(profileOne), "Database service should return true inserting new profile");

        final var profileTwo = getProfileObject("1");
        assertThrowsExactly(DatabaseException.class, () -> databaseService.addProfile(profileTwo), "Database service should throw DatabaseException inserting duplicate profile");

        final DatabaseException databaseException = assertThrows(DatabaseException.class, () -> databaseService.addProfile(profileTwo));
        assertEquals("User with %s '%s' already exists".formatted("id", profileTwo.getId()), databaseException.getMessage(), "Exception message didn't match");
        assertEquals(409, databaseException.code(), "DatabaseException code didn't match");
    }

    @Test
    void addProfile_ShouldThrowDatabaseExceptionWithExactMessageAndCode_WhenInsertingDuplicateProfileWithSameEmail() {
        final var email = "foo@bar.test";

        final var profileOne = getProfileObject("1", email);
        assertTrue(databaseService.addProfile(profileOne), "Database service should return true inserting new profile");

        final var profileTwo = getProfileObject("2", email);
        assertThrowsExactly(DatabaseException.class, () -> databaseService.addProfile(profileTwo), "Database service should throw DatabaseException inserting duplicate profile");

        final DatabaseException databaseException = assertThrows(DatabaseException.class, () -> databaseService.addProfile(profileTwo));
        assertEquals("User with %s '%s' already exists".formatted("email", email), databaseException.getMessage(), "Exception message didn't match");
        assertEquals(409, databaseException.code(), "DatabaseException code didn't match");
    }

    @Test
    void addProfile_ShouldThrowNullPointerException_WhenNullProfileInstanceIsPassed() {
        assertThrowsExactly(NullPointerException.class, () -> databaseService.addProfile(null), "addProfile should throw NullPointerException");
    }

    @Test
    void findByProfileId_ShouldReturnEmptyOptional_WhenProfileDoesNotExistsWithId() {
        assertThat(databaseService.findByProfileId("1")).isEmpty();
    }

    @Test
    void findByProfileId_ShouldNotReturnEmptyOptional_WhenProfileExistsWithId() {
        assertTrue(databaseService.addProfile(getProfileObject("1")), "addProfile should return true");
        assertThat(databaseService.findByProfileId("1")).isNotEmpty();
    }

    @Test
    void findByProfileId_ShouldThrowNullPointerException_WhenNullProfileIdIsPassed() {
        assertThrowsExactly(NullPointerException.class, () -> databaseService.findByProfileId(null), "findByProfileId should throw NullPointerException");
    }

    @Test
    void findByProfileId_ShouldReturnEmptyOptional() {
        assertThat(databaseService.findByProfileId("1")).isEmpty();
    }

    @Test
    void deleteProfileById_ShouldReturnTrue_WhenProfileExistsForGivenIdAndIsDeletedSuccessfully() {
        assertTrue(databaseService.addProfile(getProfileObject("1")), "addProfile should return true");
        assertTrue(databaseService.deleteProfileById("1"), "deleteProfileById should return true");
    }

    @Test
    void deleteProfileById_ShouldReturnFalse_WhenProfileDoesNotExistsForGivenId() {
        assertFalse(databaseService.deleteProfileById("1"), "deleteProfileById should return true");
    }

    @Test
    void deleteProfileById_ShouldThrowNullPointerException_WhenNullProfileIdIsSupplied() {
        assertThrowsExactly(NullPointerException.class, () -> databaseService.deleteProfileById(null), "deleteProfileById should throw NullPointerException");
    }

    @Test
    void updatePassword_ShouldThrowNullPointerException_WhenNullProfileIdOrNullEncryptedPasswordIsSupplied() {
        assertThrowsExactly(NullPointerException.class, () -> databaseService.updatePassword(null, ""), "updatePassword should throw NullPointerException when profileId is null");
        assertThrowsExactly(NullPointerException.class, () -> databaseService.updatePassword("", null), "updatePassword should throw NullPointerException when encryptedPassword is null");
    }

    @Test
    void updatePassword_ShouldReturnFalse_WhenProfileDoesNotExistsWithProvidedProfileId() {
        assertFalse(databaseService.updatePassword("1", "some_encrypted_password"));
    }

    @Test
    void updatePassword_ShouldReturnTrue_WhenPasswordIsSuccessfullyUpdated() {
        final var profileId = "1";
        final var newEncryptedPassword = "some-encrypted-password";

        assertTrue(databaseService.addProfile(getProfileObject("1")), "addProfile should return true");
        assertTrue(databaseService.updatePassword(profileId, newEncryptedPassword));

        final Optional<Profile> profile = databaseService.findByProfileId(profileId);
        assertThat(profile).isNotEmpty();

        assertEquals(newEncryptedPassword, profile.get().getPassword(), "Updated password should match");
    }

}
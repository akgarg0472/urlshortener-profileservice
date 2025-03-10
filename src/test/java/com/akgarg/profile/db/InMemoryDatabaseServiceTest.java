package com.akgarg.profile.db;

import com.akgarg.profile.profile.v1.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Optional;

import static com.akgarg.profile.db.DatabaseTestUtil.getProfileObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class InMemoryDatabaseServiceTest {

    private InMemoryDatabaseService databaseService;

    @BeforeEach
    void setUp() {
        databaseService = new InMemoryDatabaseService();
        assertNotNull(databaseService, "Database service can't be null");
    }

    @Test
    void addProfile_ShouldReturnTrue_IfProfileNotExistsInDatabase() {
        assertTrue(databaseService.addProfile(getProfileObject("1")), "addProfile() returns false when inserting new profile, expected true");
        assertTrue(databaseService.addProfile(getProfileObject("2")), "addProfile() returns false when inserting new profile, expected true");
    }

    @Test
    void addProfile_ShouldReturnFalse_WhenInsertingDuplicateProfileInstance() {
        final var profile = getProfileObject("1");
        assertTrue(databaseService.addProfile(profile), "addProfile() should return true when inserting new profile");
        assertFalse(databaseService.addProfile(profile), "addProfile() should return false when inserting duplicate profile");
    }

    @Test
    void findByProfileId_shouldReturnEmptyOptional_WhenProfileNotExistsByProfileId() {
        final var profileId = "1";
        final Optional<Profile> profile = databaseService.findByProfileId(profileId);
        assertThat(profile).isEmpty();
    }

    @Test
    void findByProfileId_ShouldReturnOptionalWithProfileInstance_WhenProfileExistsByProfileId() {
        final var profile = getProfileObject("1");
        assertTrue(databaseService.addProfile(profile), "addProfile() should return true when inserting new profile");
        final var profileOptional = databaseService.findByProfileId(profile.getId());
        assertThat(profileOptional).isPresent().hasValueSatisfying(p -> assertThat(p).isEqualTo(profile));
    }

    @Test
    void updateProfile_ShouldReturnFalse_WhenProfileDoesNotExist() {
        assertDoesNotThrow(() -> databaseService.updateProfile(getProfileObject("1")), "updateProfile should return false when profile doesn't exists");
    }

    @Test
    void updateProfile_ShouldReturnTrue_WhenProfileExists() {
        final var profile = getProfileObject("1");
        assertDoesNotThrow(() -> databaseService.addProfile(profile), "addProfile() should return true when inserting new profile");
        assertDoesNotThrow(() -> databaseService.updateProfile(profile), "updateProfile should return true when profile exists");
    }

    @Test
    void deleteProfile_ShouldReturnFalseWhenProfileDoesNotExist() {
        assertDoesNotThrow(() -> databaseService.deleteProfileById("1"), "deleteProfileById should return false when profile doesn't exists");
    }

    @Test
    void deleteProfile_ShouldReturnTrueWhenProfileExists() {
        final var profileId = "1";
        assertTrue(databaseService.addProfile(getProfileObject(profileId)), "addProfile should return true while adding new profile");
        assertThat(databaseService.findByProfileId(profileId)).isNotEmpty();
        assertDoesNotThrow(() -> databaseService.deleteProfileById(profileId), "deleteProfileById should return true when profile exists");
        assertThat(databaseService.findByProfileId(profileId)).isEmpty();
    }

    @Test
    void addProfile_ShouldThrowException_WhenProfileIsNull() {
        assertThrowsExactly(NullPointerException.class, () -> databaseService.addProfile(null), "addProfile should throw NullPointerException when profile instance is null");
    }

    @Test
    void findByProfileId_ShouldThrowException_WhenProfileIdIsNull() {
        assertThrowsExactly(NullPointerException.class, () -> databaseService.findByProfileId(null), "findByProfileId should throw NullPointerException when profileId is null");
    }

    @Test
    void updateProfile_ShouldThrowException_WhenProfileIsNull() {
        assertThrowsExactly(NullPointerException.class, () -> databaseService.updateProfile(null), "updateProfile should throw NullPointerException when profile instance is null");
    }

    @Test
    void deleteProfileById_ShouldThrowException_WhenProfileIdIsNull() {
        assertThrowsExactly(NullPointerException.class, () -> databaseService.deleteProfileById(null), "deleteProfileById should throw NullPointerException when profileId is null");
    }

    @Test
    void updatePassword_ShouldThrowException_WhenEitherOfParamsIsNull() {
        assertThrowsExactly(NullPointerException.class, () -> databaseService.updatePassword(null, ""), "updatePassword should throw NullPointerException when profileId is null");
        assertThrowsExactly(NullPointerException.class, () -> databaseService.updatePassword("", null), "updatePassword should throw NullPointerException when encryptedPassword is null");
        assertThrowsExactly(NullPointerException.class, () -> databaseService.updatePassword(null, null), "updatePassword should throw NullPointerException when both params are null");
    }

}

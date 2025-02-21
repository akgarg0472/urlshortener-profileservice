package com.akgarg.profile.db;

import com.akgarg.profile.profile.v1.Profile;
import com.github.javafaker.Faker;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class DatabaseTestUtil {

    private static final Faker faker = new Faker();

    static JdbcTemplate getJdbcTemplate() {
        final var jdbcTemplate = new JdbcTemplate(configureDatasource());
        assertNotNull(jdbcTemplate, "JdbcTemplate is null");
        ping(jdbcTemplate);
        initializeDatabaseTable(jdbcTemplate);
        return jdbcTemplate;
    }

    static void initializeDatabaseTable(final JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute(
                """
                         CREATE TABLE `users` (
                          `id` varchar(128) NOT NULL,
                          `username` varchar(16) NOT NULL,
                          `email` varchar(255) NOT NULL,
                          `password` varchar(255) NOT NULL,
                          `scopes` varchar(32) NOT NULL,
                          `name` varchar(255) NOT NULL,
                          `bio` text,
                          `phone` varchar(20) DEFAULT '',
                          `premium_account` boolean DEFAULT false,
                          `city` varchar(50) DEFAULT '',
                          `state` varchar(50) DEFAULT '',
                          `country` varchar(50) DEFAULT '',
                          `zipcode` varchar(16) DEFAULT '',
                          `business_details` text,
                          `forgot_password_token` varchar(255) DEFAULT '',
                          `last_password_changed_at` bigint DEFAULT NULL,
                          `last_login_at` bigint DEFAULT NULL,
                          `is_deleted` boolean DEFAULT false,
                          `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                          `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `username` (`username`),
                          UNIQUE KEY `email` (`email`),
                          KEY `idx_email` (`email`)
                        );
                        """
        );
    }

    private static DataSource configureDatasource() {
        final var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/urlshortener_users_test?createDatabaseIfNotExist=true");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    private static void ping(final JdbcTemplate jdbcTemplate) {
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);
    }

    static Profile getProfileObject(final String profileId) {
        final var profile = new Profile();
        profile.setId(profileId);
        profile.setEmail(faker.internet().emailAddress());
        profile.setPassword(faker.random().toString());
        profile.setName(faker.name().name());
        profile.setScopes("test");
        return profile;
    }

    static Profile getProfileObject(final String profileId, final String email) {
        final var profile = getProfileObject(profileId);
        profile.setEmail(email);
        return profile;
    }

}

package com.akgarg.profile.configs;

import com.akgarg.profile.db.DatabaseService;
import com.akgarg.profile.db.InMemoryDatabaseService;
import com.akgarg.profile.db.MysqlDatabaseService;
import com.akgarg.profile.image.CloudImageService;
import com.akgarg.profile.image.ImageService;
import com.akgarg.profile.image.LocalStorageImageService;
import com.akgarg.profile.notification.KafkaNotificationService;
import com.akgarg.profile.notification.NotificationService;
import com.akgarg.profile.notification.VoidNotificationService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Properties;

@Configuration
public class BeanConfigs {

    @Profile("dev")
    @Bean("databaseService")
    public DatabaseService inMemoryDatabaseService() {
        return new InMemoryDatabaseService();
    }

    @Profile("prod")
    @Bean("databaseService")
    public DatabaseService mysqlDatabaseService(final JdbcTemplate jdbcTemplate) {
        return new MysqlDatabaseService(jdbcTemplate);
    }

    @Profile("prod")
    @Bean("jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        final Properties properties = new HikariConfig("/hikari.properties")
                .getDataSourceProperties();

        final HikariDataSource datasource = new HikariDataSource();
        datasource.setDriverClassName(properties.getProperty("driverClassName"));
        datasource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        datasource.setUsername(properties.getProperty("username"));
        datasource.setPassword(properties.getProperty("password"));
        datasource.setPoolName(properties.getProperty("poolName"));
        datasource.setMaximumPoolSize(Integer.parseInt(properties.getProperty("maxPoolSize")));
        datasource.setMinimumIdle(Integer.parseInt(properties.getProperty("minIdle")));
        datasource.setMaxLifetime(Long.parseLong(properties.getProperty("maxLifetime")));
        datasource.setConnectionTimeout(Long.parseLong(properties.getProperty("connectionTimeout")));
        datasource.setIdleTimeout(Long.parseLong(properties.getProperty("idleTimeout")));
        datasource.setLeakDetectionThreshold(Long.parseLong(properties.getProperty("leakDetectionThreshold")));
        datasource.setConnectionTestQuery(properties.getProperty("connectionTestQuery"));

        return new JdbcTemplate(datasource);
    }

    @Profile("dev")
    @Bean("notificationService")
    public NotificationService voidNotificationService() {
        return new VoidNotificationService();
    }

    @Profile("prod")
    @Bean("notificationService")
    public NotificationService notificationService(final KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaNotificationService(kafkaTemplate);
    }

    @Profile("dev")
    @Bean("imageService")
    public ImageService localStorageImageService() {
        return new LocalStorageImageService();
    }

    @Profile("prod")
    @Bean("imageService")
    public ImageService cloudImageService() {
        return new CloudImageService();
    }

}

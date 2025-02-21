package com.akgarg.profile.configs;

import com.akgarg.profile.db.DatabaseService;
import com.akgarg.profile.db.InMemoryDatabaseService;
import com.akgarg.profile.db.MysqlDatabaseService;
import com.akgarg.profile.db.repo.ProfileRepository;
import com.akgarg.profile.image.CloudinaryImageService;
import com.akgarg.profile.image.ImageService;
import com.akgarg.profile.image.LocalStorageImageService;
import com.akgarg.profile.notification.KafkaNotificationService;
import com.akgarg.profile.notification.NotificationService;
import com.akgarg.profile.notification.VoidNotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfigs {

    private static final int PASSWORD_ENCODER_STRENGTH = 10;

    @Profile("dev")
    @Bean("databaseService")
    public DatabaseService inMemoryDatabaseService() {
        return new InMemoryDatabaseService();
    }

    @Profile("prod")
    @Bean("databaseService")
    public DatabaseService mysqlDatabaseService(final ProfileRepository profileRepository) {
        return new MysqlDatabaseService(profileRepository);
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
    public ImageService cloudImageService(final Environment environment) {
        return new CloudinaryImageService(environment);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(PASSWORD_ENCODER_STRENGTH);
    }

}

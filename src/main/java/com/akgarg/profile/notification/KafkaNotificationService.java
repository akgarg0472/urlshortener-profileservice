package com.akgarg.profile.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

public class KafkaNotificationService extends AbstractNotificationService {

    private static final Logger LOGGER = LogManager.getLogger(KafkaNotificationService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value(value = "${kafka.notification.topic.name}")
    private String notificationTopicName;

    public KafkaNotificationService(final KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void publishEvent(final NotificationEvent notificationEvent) {
        serializeEvent(notificationEvent)
                .ifPresent(eventJson -> kafkaTemplate.send(notificationTopicName, eventJson)
                        .whenComplete((s1, s2) -> LOGGER.info("{} : {}", s1, s2)));
    }

    private Optional<String> serializeEvent(final NotificationEvent notificationEvent) {
        try {
            return Optional.of(objectMapper.writeValueAsString(notificationEvent));
        } catch (Exception e) {
            LOGGER.error("Error occurred while serializing notification event: {}", e.getMessage());
            return Optional.empty();
        }
    }

}

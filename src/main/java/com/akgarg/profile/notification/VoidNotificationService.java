package com.akgarg.profile.notification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VoidNotificationService extends AbstractNotificationService {

    private static final Logger LOGGER = LogManager.getLogger(VoidNotificationService.class);

    @Override
    public void publishEvent(final NotificationEvent notificationEvent) {
        LOGGER.info("Publishing event: {}", notificationEvent);
    }

}

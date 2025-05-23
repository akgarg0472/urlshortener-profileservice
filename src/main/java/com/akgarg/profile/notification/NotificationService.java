package com.akgarg.profile.notification;

public interface NotificationService {

    void publishEvent(NotificationEvent notificationEvent);

    void sendPasswordChangedSuccessEmail(String email, final String name);

}

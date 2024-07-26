package com.akgarg.profile.notification;

import java.util.Arrays;
import java.util.Objects;

public record NotificationEvent(String[] recipients, String name, String subject, String body, boolean isHtml,
                                NotificationType notificationType) {

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o instanceof final NotificationEvent event) {
            return isHtml == event.isHtml &&
                    Objects.equals(name, event.name) &&
                    Objects.equals(body, event.body) &&
                    Objects.equals(subject, event.subject) &&
                    Arrays.equals(recipients, event.recipients) &&
                    notificationType == event.notificationType;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(recipients);
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(subject);
        result = 31 * result + Objects.hashCode(body);
        result = 31 * result + Boolean.hashCode(isHtml());
        result = 31 * result + Objects.hashCode(notificationType);
        return result;
    }

    @Override
    public String toString() {
        return "NotificationEvent{" +
                "recipients=" + Arrays.toString(recipients) +
                ", name='" + name + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", isHtml=" + isHtml +
                ", notificationType=" + notificationType +
                '}';
    }
}
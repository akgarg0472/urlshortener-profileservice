package com.akgarg.profile.notification;

import java.util.Arrays;
import java.util.Objects;

public final class NotificationEvent {

    private final String[] recipients;
    private final String subject;
    private final String body;
    private final boolean isHtml;
    private final NotificationType notificationType;

    NotificationEvent(
            String[] recipients,
            String subject,
            String body,
            boolean isHtml,
            NotificationType notificationType
    ) {
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.isHtml = isHtml;
        this.notificationType = notificationType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(recipients), subject, body, isHtml, notificationType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (NotificationEvent) obj;
        return Arrays.equals(this.recipients, that.recipients) &&
                Objects.equals(this.subject, that.subject) &&
                Objects.equals(this.body, that.body) &&
                this.isHtml == that.isHtml &&
                Objects.equals(this.notificationType, that.notificationType);
    }

    @Override
    public String toString() {
        return "NotificationEvent[" +
                "recipients=" + Arrays.toString(recipients) + ", " +
                "subject=" + subject + ", " +
                "body=" + body + ", " +
                "isHtml=" + isHtml + ", " +
                "notificationType=" + notificationType + ']';
    }


}
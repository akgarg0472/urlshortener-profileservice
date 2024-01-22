package com.akgarg.profile.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
class NotificationEvent {

    private final String[] recipients;
    private final String subject;
    private final String body;
    private final boolean isHtml;
    private final NotificationType notificationType;

}
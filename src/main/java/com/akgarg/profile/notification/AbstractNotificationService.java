package com.akgarg.profile.notification;

abstract class AbstractNotificationService implements NotificationService {

    private static final String PASSWORD_CHANGED_SUCCESSFULLY_EMAIL_BODY = "<div style='font-family:Arial,sans-serif;margin:0;padding:0;background: linear-gradient(to bottom, #f7f8f9, #ffffff)!important;max-width:600px;margin:20px auto;padding:20px;background-color:#fff;border-radius:5px;box-shadow:0 0 10px rgba(0,0,0,0.1);color:#333;text-align:center;'><p style='font-size:16px;text-align:left'>Dear $USER_NAME,</p><p style='text-align:left;line-height:24px;font-size:16px;'>We are notifying you that the password for your account associated with <span style='color:#15c'>$USER_EMAIL</span> has been successfully changed. If you made this change, no further action is needed.</p><p style='text-align:left;margin-top:24px;line-height:24px;font-size:16px;'>If you did not request this change, we recommend that you immediately reset your password and reach out to our support team through our support desk. Rest assured, no other changes have been made to your account.</p><div style='text-align:left;font-size:16px;margin-top:24px;'>- URLShortener Team</div><div style='padding:10px;border-radius:0 0 5px 5px;margin-top:20px;font-size:12px;line-height:18px;'>UrlShortener is a hobby project by Akhilesh Garg.</div></div>";

    @Override
    public void sendPasswordChangedSuccessEmail(final String email, final String name) {
        final NotificationEvent notificationEvent = new NotificationEvent(
                new String[]{email},
                name,
                "Password changed successfully 🎉",
                PASSWORD_CHANGED_SUCCESSFULLY_EMAIL_BODY
                        .replace("$USER_EMAIL", email)
                        .replace("$USER_NAME", name),
                true,
                NotificationType.EMAIL
        );
        publishEvent(notificationEvent);
    }

}

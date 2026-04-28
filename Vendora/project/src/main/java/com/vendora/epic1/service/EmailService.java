package com.vendora.epic1.service;

public interface EmailService {

    void sendEmail(String to, String subject, String body);

    void sendVerificationEmail(String to, String token);

    void sendPasswordResetEmail(String to, String token);

    /** Sends the approval email with a clickable complete-registration URL. */
    void sendPartnershipApprovalEmail(String to, String completeRegistrationUrl);

    /** Sent immediately after a partnership form is submitted — "max 3 days" notice. */
    void sendPartnershipAcknowledgementEmail(String to, String applicantName);

    /** Sent when admin rejects a partnership application. */
    void sendPartnershipRejectionEmail(String to, String applicantName, String note);
}
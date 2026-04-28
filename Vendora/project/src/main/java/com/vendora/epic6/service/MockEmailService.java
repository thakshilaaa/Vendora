package com.vendora.epic6.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Default development-mode email service.
 * Logs the email contents instead of sending so we don't need SMTP creds.
 *
 * To switch to real email, add `vendora.email.real=true` and provide a
 * different EmailService bean.
 */
@Service
@ConditionalOnProperty(name = "vendora.email.real", havingValue = "false", matchIfMissing = true)
public class MockEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(MockEmailService.class);

    @Override
    public void sendApprovalEmail(String toEmail, String companyName, String tempPassword) {
        log.info("[MOCK EMAIL] -> {}", toEmail);
        log.info("Subject: Welcome to Vendora, {} — your account is approved!", companyName);
        log.info("Body: You can now sign in at /login.");
        log.info("      Email: {}", toEmail);
        log.info("      Temporary password: {}", tempPassword);
        log.info("      Please change your password on first login.");
    }

    @Override
    public void sendRejectionEmail(String toEmail, String companyName) {
        log.info("[MOCK EMAIL] -> {}", toEmail);
        log.info("Subject: Vendora supplier application update");
        log.info("Body: Hello {} — unfortunately we are unable to approve your application at this time.",
                companyName);
    }
}

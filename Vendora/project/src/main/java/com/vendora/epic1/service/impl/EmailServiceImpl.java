package com.vendora.epic1.service.impl;

import com.vendora.epic1.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    @Override
    public void sendEmail(String to, String subject, String body) {
        if (!StringUtils.hasText(to)) {
            log.error("Refusing to send email: empty recipient address");
            throw new IllegalArgumentException("Recipient address is required");
        }
        if (!StringUtils.hasText(mailFrom)) {
            log.error("spring.mail.username is not set; cannot send email to {}. Set MAIL_USERNAME / spring.mail.username or add application-local.properties — see application-local.properties.example", to);
            throw new IllegalStateException("Mail is not configured: missing spring.mail.username");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            log.info("Email sent successfully: from={} to={} subject={}", mailFrom, to, subject);
        } catch (MessagingException e) {
            log.error("Failed to build MIME message for to={} subject={}", to, subject, e);
            throw new MailSendException("Failed to build email message", e);
        } catch (MailException e) {
            log.error("Failed to send email to={} subject={} — check Gmail app password, 2-Step Verification, and spring.mail properties", to, subject, e);
            throw e;
        }
    }

    @Override
    public void sendVerificationEmail(String to, String token) {
        String subject = "Vendora | Verify your email";
        String verifyPath = appBaseUrl.replaceAll("/$", "")
                + "/verify-email?t="
                + java.net.URLEncoder.encode(token, java.nio.charset.StandardCharsets.UTF_8);
        String body = """
                <div style="font-family: Arial, sans-serif; border: 1px solid #d4a373; padding: 20px; border-radius: 10px; max-width: 500px; margin: auto;">
                    <h2 style="color: #d4a373; text-align: center;">Welcome to Vendora</h2>
                    <p>Please verify your email address by clicking the button below. The link is valid for <strong>5 minutes</strong>.</p>
                    <div style="text-align: center; margin: 24px 0;">
                        <a href="%1$s" style="background-color: #d4a373; color: white; padding: 14px 28px; border-radius: 6px; text-decoration: none; font-weight: bold;">Verify email</a>
                    </div>
                    <p style="font-size: 12px; color: #666; word-break: break-all;">If the button does not work, copy this link into your browser:<br><a href="%1$s">%1$s</a></p>
                    <p style="font-size: 12px; color: #999;">This link contains a secret token — do not forward this email. If you did not register, ignore this message.</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 11px; color: #999; text-align: center;">&copy; 2026 Vendora Beauty Store | Sri Lanka</p>
                </div>
                """.formatted(verifyPath);

        sendEmail(to, subject, body);
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Vendora | Reset your password";
        String body = """
                <div style="font-family: Arial, sans-serif; border: 1px solid #d4a373; padding: 20px; border-radius: 10px; max-width: 500px; margin: auto;">
                    <h2 style="color: #d4a373; text-align: center;">Password reset</h2>
                    <p>We received a request to reset your password. Use the code below in the app:</p>
                    <div style="background: #fefae0; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 4px; color: #333; border-radius: 5px; border: 1px dashed #d4a373; margin: 20px 0;">
                        %s
                    </div>
                    <p style="margin-top: 20px; font-size: 12px; color: #666; text-align: center;">If you did not request this, you can ignore this email.</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 11px; color: #999; text-align: center;">&copy; 2026 Vendora Beauty Store | Sri Lanka</p>
                </div>
                """.formatted(token);

        sendEmail(to, subject, body);
    }

    @Override
    public void sendPartnershipApprovalEmail(String to, String completeRegistrationUrl) {
        String subject = "Vendora | Partnership application approved";
        String body = """
                <div style="font-family: Arial, sans-serif; border: 1px solid #d4a373; padding: 20px; border-radius: 10px; max-width: 520px; margin: auto;">
                    <h2 style="color: #d4a373; text-align: center;">Congratulations</h2>
                    <p>Your partnership application with <strong>Vendora</strong> has been approved.</p>
                    <p>Click the button below to set your password and activate your account:</p>
                    <div style="text-align: center; margin: 24px 0;">
                        <a href="%1$s" style="background-color: #d4a373; color: white; padding: 14px 28px; border-radius: 6px; text-decoration: none; font-weight: bold;">Complete registration</a>
                    </div>
                    <p style="font-size: 12px; color: #666; text-align: center;">This link expires in <strong>72 hours</strong>.</p>
                    <p style="font-size: 11px; color: #aaa; text-align: center; word-break: break-all;">%1$s</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 11px; color: #999; text-align: center;">&copy; 2026 Vendora Beauty Store | Sri Lanka</p>
                </div>
                """.formatted(completeRegistrationUrl);

        sendEmail(to, subject, body);
    }

    @Override
    public void sendPartnershipAcknowledgementEmail(String to, String applicantName) {
        String subject = "Vendora | Application received";
        String body = """
                <div style="font-family: Arial, sans-serif; border: 1px solid #d4a373; padding: 20px; border-radius: 10px; max-width: 520px; margin: auto;">
                    <h2 style="color: #d4a373; text-align: center;">Application received</h2>
                    <p>Dear <strong>%s</strong>,</p>
                    <p>Thank you for applying to partner with <strong>Vendora</strong>. We will review your application within <strong>3 business days</strong>.</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 11px; color: #999; text-align: center;">&copy; 2026 Vendora Beauty Store | Sri Lanka</p>
                </div>
                """.formatted(applicantName);

        sendEmail(to, subject, body);
    }

    @Override
    public void sendPartnershipRejectionEmail(String to, String applicantName, String note) {
        String reasonSection = (note != null && !note.isBlank())
                ? "<p><strong>Reason:</strong> " + org.springframework.web.util.HtmlUtils.htmlEscape(note) + "</p>"
                : "";
        String subject = "Vendora | Partnership application update";
        String body = """
                <div style="font-family: Arial, sans-serif; border: 1px solid #d4a373; padding: 20px; border-radius: 10px; max-width: 520px; margin: auto;">
                    <h2 style="color: #c0392b; text-align: center;">Application not approved</h2>
                    <p>Dear <strong>%s</strong>,</p>
                    <p>We regret to inform you that your partnership application was not approved at this time.</p>
                    %s
                    <p>You are welcome to re-apply in the future.</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="font-size: 11px; color: #999; text-align: center;">&copy; 2026 Vendora Beauty Store | Sri Lanka</p>
                </div>
                """.formatted(applicantName, reasonSection);

        sendEmail(to, subject, body);
    }
}

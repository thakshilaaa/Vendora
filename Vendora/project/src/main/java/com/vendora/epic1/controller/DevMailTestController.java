package com.vendora.epic1.controller;

import com.vendora.epic1.service.EmailService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Only registered when app.mail.test-endpoint=true. POST a JSON body: {"to":"you@gmail.com"}.
 */
@RestController
@RequestMapping("/api/dev/mail")
@ConditionalOnProperty(name = "app.mail.test-endpoint", havingValue = "true")
public class DevMailTestController {

    private final EmailService emailService;

    public DevMailTestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTest(@Valid @RequestBody TestMailBody body) {
        emailService.sendEmail(
                body.getTo().trim().toLowerCase(),
                "Vendora mail test",
                "<p>Your Gmail SMTP settings are working.</p><p>Sent at " + java.time.Instant.now() + "</p>");
        return ResponseEntity.ok(Map.of("status", "sent", "to", body.getTo().trim().toLowerCase()));
    }

    @Data
    public static class TestMailBody {
        @NotBlank
        @Email
        private String to;
    }
}

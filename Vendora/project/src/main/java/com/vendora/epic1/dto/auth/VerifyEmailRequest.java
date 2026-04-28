package com.vendora.epic1.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Verification is driven only by the server-issued token (stored in {@code email_verification_tokens}).
 * The client does not need to (and should not) send the email for verification — the user is loaded from the token row.
 */
@Getter
@Setter
public class VerifyEmailRequest {
    @NotBlank(message = "Verification token is required")
    @Size(max = 256)
    private String token;
}

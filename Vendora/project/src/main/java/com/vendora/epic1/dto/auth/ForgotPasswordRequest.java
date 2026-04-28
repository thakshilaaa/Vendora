package com.vendora.epic1.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {
    @NotBlank
    @Email
    private String email;

    /**
     * Required when more than one account uses this email.
     */
    private String phone;
}
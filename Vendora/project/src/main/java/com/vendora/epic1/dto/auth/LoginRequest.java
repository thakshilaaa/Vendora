package com.vendora.epic1.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;

    /**
     * When several accounts share the same email, sign-in requires phone to pick the right account.
     */
    private String phone;
}
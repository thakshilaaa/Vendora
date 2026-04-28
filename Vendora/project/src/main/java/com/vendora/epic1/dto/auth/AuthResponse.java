package com.vendora.epic1.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private boolean success;
    private String message;
    /** Unique sign-in and JWT identifier (U + 7 hex). Shown after registration. */
    private String userCode;
}
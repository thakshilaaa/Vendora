package com.vendora.epic1.dto.auth;

import com.vendora.epic1.model.enums.RoleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String refreshToken;
    private Long userId;
    private RoleType role;
    private String dashboardPath;
    /** Unique sign-in id when email is shared across accounts. */
    private String userCode;
}
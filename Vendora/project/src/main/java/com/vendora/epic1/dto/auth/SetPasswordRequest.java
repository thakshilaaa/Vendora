package com.vendora.epic1.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetPasswordRequest {
    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 6, max = 20)
    private String newPassword;

    // Used by the complete-registration flow to verify the partner
    // is using the same email that was submitted in the partnership form
    private String email;
}
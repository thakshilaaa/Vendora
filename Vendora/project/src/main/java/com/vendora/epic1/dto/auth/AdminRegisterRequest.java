package com.vendora.epic1.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(max = 15)
    @Pattern(regexp = "^(\\+94|0)(7[0-9]{8}|[12345689][0-9]{8})$",
            message = "Enter a valid Sri Lankan phone number (e.g. 0712345678)")
    private String phone;

    @NotBlank
    @Size(min = 9, max = 12)
    @Pattern(regexp = "^([0-9]{9}[VvXx]|[0-9]{12})$", message = "Invalid NIC format")
    private String nic;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}
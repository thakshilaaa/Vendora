package com.vendora.epic1.dto.auth;

import com.vendora.epic1.model.enums.District;
import com.vendora.epic1.model.enums.Province;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRegisterRequest {
    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;

    /**
     * Sri Lankan mobile/landline after normalizing; matches common DB check constraints.
     */
    @NotBlank
    @Size(max = 15)
    @Pattern(regexp = "^(\\+94|0)(7[0-9]{8}|[12345689][0-9]{8})$",
            message = "Enter a valid Sri Lankan phone number (e.g. 0712345678)")
    private String phone;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    @NotBlank
    @Size(max = 12)
    @Pattern(regexp = "^([0-9]{9}[VvXx]|[0-9]{12})$", message = "Invalid NIC format")
    private String nic;

    @NotBlank
    @Size(max = 255)
    private String addressLine1;

    @Size(max = 255)
    private String addressLine2;

    @NotBlank
    @Size(max = 100)
    private String city;

    @NotNull
    private District district;

    @NotNull
    private Province province;

    @Size(max = 10)
    private String postalCode;
}

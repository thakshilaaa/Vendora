package com.vendora.statics.dto.partnership;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPartnershipRequest {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 150, message = "Company name must be between 2 and 150 characters")
    private String companyName;

    @NotBlank(message = "Contact person name is required")
    @Size(min = 2, max = 100, message = "Contact person name must be between 2 and 100 characters")
    private String contactPersonName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+94|0)?[0-9]{9,10}$", message = "Invalid phone number")
    private String phoneNumber;

    @NotBlank(message = "Business address is required")
    @Size(max = 255, message = "Business address cannot exceed 255 characters")
    private String businessAddress;

    @NotBlank(message = "Service areas are required")
    @Size(max = 255, message = "Service areas cannot exceed 255 characters")
    private String serviceAreas;

    @NotBlank(message = "Fleet size is required")
    @Size(max = 50, message = "Fleet size cannot exceed 50 characters")
    private String fleetSize;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

}
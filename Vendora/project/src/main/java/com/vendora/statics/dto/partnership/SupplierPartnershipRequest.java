package com.vendora.statics.dto.partnership;

import jakarta.persistence.criteria.CriteriaBuilder;
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
public class SupplierPartnershipRequest {

    @NotBlank(message = "Business name is required")
    @Size(min = 2, max = 150, message = "Business name must be between 2 and 150 characters")
    private String businessName;

    @NotBlank(message = "Contact person name is required")
    @Size(min = 2, max = 100, message = "Contact person name must be between 2 and 100 characters")
    private String contactPersonName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(\\+94|0)?[0-9]{9,10}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;

    @NotBlank(message = "Business address is required")
    @Size(max = 255, message = "Business address cannot exceed 255 characters")
    private String businessAddress;

    @NotBlank(message = "Business registration number is required")
    @Size(max = 100, message = "Business registration number cannot exceed 100 characters")
    private String businessRegistrationNumber;

    @NotBlank(message = "Product category is required")
    @Size(max = 100, message = "Product category cannot exceed 100 characters")
    private String productCategory;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

}
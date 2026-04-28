package com.vendora.epic6.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SupplierRegistrationDto {

    @NotBlank(message = "Company name is required")
    @Size(max = 120)
    private String companyName;

    @NotBlank(message = "Contact person is required")
    @Size(max = 120)
    private String contactPerson;

    @NotBlank @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(min = 7, max = 20)
    private String phone;

    @NotBlank(message = "Address is required")
    @Size(max = 500)
    private String address;

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}

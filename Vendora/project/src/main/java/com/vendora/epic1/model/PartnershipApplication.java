package com.vendora.epic1.model;

import com.vendora.epic1.model.enums.ApplicantType;
import com.vendora.epic1.model.enums.PartnershipStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity(name = "Epic1PartnershipApplication")
@Table(name = "partnership_applications")
@Getter
@Setter
public class PartnershipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicantType applicantType;

    @NotBlank
    private String fullName;

    private String contactPersonName;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;

    private String phoneNumber;
    private String nic;

    // Address
    private String addressLine1;
    private String businessAddress;
    private String province;
    private String district;
    private String city;
    private String postalCode;

    // Company / Supplier fields (nullable for delivery)
    private String businessName;
    private String businessRegNumber;
    private String tinNumber;
    private String productCategory;

    @Column(columnDefinition = "TEXT")
    private String productDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnershipStatus status = PartnershipStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String reviewNote;

    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public PartnershipApplication() {}

    // Getters & Setters

    public Long getId() { return id; }

    public ApplicantType getApplicantType() { return applicantType; }
    public void setApplicantType(ApplicantType applicantType) { this.applicantType = applicantType; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getContactPersonName() { return contactPersonName; }
    public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phoneNumber; }
    public void setPhone(String phone) { this.phoneNumber = phone; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getBusinessRegNumber() { return businessRegNumber; }
    public void setBusinessRegNumber(String businessRegNumber) { this.businessRegNumber = businessRegNumber; }

    public String getTinNumber() { return tinNumber; }
    public void setTinNumber(String tinNumber) { this.tinNumber = tinNumber; }

    public String getProductCategory() { return productCategory; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }

    public String getProductDetails() { return productDetails; }
    public void setProductDetails(String productDetails) { this.productDetails = productDetails; }

    public PartnershipStatus getStatus() { return status; }
    public void setStatus(PartnershipStatus status) { this.status = status; }

    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

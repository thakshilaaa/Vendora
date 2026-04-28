package com.vendora.epic1.dto.partnership;

import com.vendora.epic1.model.enums.ApplicantType;
import com.vendora.epic1.model.enums.PartnershipStatus;

import java.time.LocalDateTime;

public class PartnershipApplicationResponse {

    private Long id;
    private ApplicantType applicantType;
    private String fullName;
    private String email;
    private String phone;
    private String city;
    private String district;
    private String businessName;
    private PartnershipStatus status;
    private LocalDateTime createdAt;
    private String reviewNote;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ApplicantType getApplicantType() { return applicantType; }
    public void setApplicantType(ApplicantType applicantType) { this.applicantType = applicantType; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public PartnershipStatus getStatus() { return status; }
    public void setStatus(PartnershipStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }
}

package com.vendora.statics.dto.partnership;

import com.vendora.statics.model.enums.ApplicationStatus;
import com.vendora.statics.model.enums.ApplicationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnershipApplicationResponse {
    private Long id;
    private ApplicationType applicationType;
    private ApplicationStatus status;
    private String businessName;
    private String contactPersonName;
    private String email;
    private String phoneNumber;
    private String businessAddress;
    private String businessRegistrationNumber;
    private String serviceAreas;
    private String fleetSize;
    private String productCategory;
    private String description;
    private String adminRemarks;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
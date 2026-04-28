package com.vendora.statics.dto.partnership;

import com.vendora.statics.model.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPartnershipRequest {

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    @Size(max = 1000, message = "Admin remarks cannot exceed 1000 characters")
    private String adminRemarks;
}
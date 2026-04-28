package com.vendora.statics.controller;

import com.vendora.common.ApiResponse;
import com.vendora.statics.dto.partnership.PartnershipApplicationResponse;
import com.vendora.statics.dto.partnership.ReviewPartnershipRequest;
import com.vendora.statics.service.AdminPartnershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/partnerships")
@RequiredArgsConstructor
public class AdminPartnershipController {

    private final AdminPartnershipService adminPartnershipService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PartnershipApplicationResponse>>> getAllApplications() {
        List<PartnershipApplicationResponse> response = adminPartnershipService.getAllApplications();
        return ResponseEntity.ok(ApiResponse.success("All partnership applications retrieved successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PartnershipApplicationResponse>> getApplicationById(
            @PathVariable Long id
    ) {
        PartnershipApplicationResponse response = adminPartnershipService.getApplicationById(id);
        return ResponseEntity.ok(ApiResponse.success("Partnership application retrieved successfully", response));
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<ApiResponse<PartnershipApplicationResponse>> reviewApplication(
            @PathVariable Long id,
            @Valid @RequestBody ReviewPartnershipRequest request
    ) {
        PartnershipApplicationResponse response = adminPartnershipService.reviewApplication(id, request);
        return ResponseEntity.ok(ApiResponse.success("Partnership application reviewed successfully", response));
    }
}

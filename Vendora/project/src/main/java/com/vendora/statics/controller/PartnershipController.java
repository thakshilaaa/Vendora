package com.vendora.statics.controller;

import com.vendora.common.ApiResponse;
import com.vendora.statics.dto.partnership.DeliveryPartnershipRequest;
import com.vendora.statics.dto.partnership.PartnershipApplicationResponse;
import com.vendora.statics.dto.partnership.SupplierPartnershipRequest;
import com.vendora.statics.service.PartnershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/partnerships")
@RequiredArgsConstructor
public class PartnershipController {

    private final PartnershipService partnershipService;

    @PostMapping("/supplier")
    public ResponseEntity<ApiResponse<PartnershipApplicationResponse>> applySupplierPartnership(
            @Valid @RequestBody SupplierPartnershipRequest request
    ) {
        PartnershipApplicationResponse response = partnershipService.applySupplierPartnership(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supplier partnership application submitted successfully", response));
    }

    @PostMapping("/delivery")
    public ResponseEntity<ApiResponse<PartnershipApplicationResponse>> applyDeliveryPartnership(
            @Valid @RequestBody DeliveryPartnershipRequest request
    ) {
        PartnershipApplicationResponse response = partnershipService.applyDeliveryPartnership(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Delivery partnership application submitted successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PartnershipApplicationResponse>> getApplicationById(
            @PathVariable Long id
    ) {
        PartnershipApplicationResponse response = partnershipService.getApplicationById(id);
        return ResponseEntity.ok(ApiResponse.success("Partnership application retrieved successfully", response));
    }
}

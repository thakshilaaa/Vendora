package com.vendora.epic1.controller;

import com.vendora.epic1.dto.auth.AuthResponse;
import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.partnership.ApprovePartnerRequest;
import com.vendora.epic1.dto.partnership.PartnershipApplicationRequest;
import com.vendora.epic1.dto.partnership.PartnershipApplicationResponse;
import com.vendora.epic1.dto.partnership.RejectPartnerRequest;
import com.vendora.epic1.service.PartnershipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("epic1PartnershipController")
@RequestMapping("/api/partnership")
public class PartnershipController {

    private final PartnershipService partnershipService;

    public PartnershipController(PartnershipService partnershipService) {
        this.partnershipService = partnershipService;
    }

    @PostMapping("/apply")
    public ResponseEntity<AuthResponse> submitApplication(@RequestBody PartnershipApplicationRequest request) {
        // Files are ignored for now as requested
        return ResponseEntity.ok(partnershipService.submitApplication(request));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PartnershipApplicationResponse>> getPendingApplications() {
        return ResponseEntity.ok(partnershipService.getPendingApplications());
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> approveApplication(@RequestBody ApprovePartnerRequest request) {
        return ResponseEntity.ok(partnershipService.approveApplication(request.getApplicationId()));
    }

    @PostMapping("/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> rejectApplication(@RequestBody RejectPartnerRequest request) {
        return ResponseEntity.ok(partnershipService.rejectApplication(request));
    }
}

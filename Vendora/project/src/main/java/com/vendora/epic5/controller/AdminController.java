package com.vendora.epic5.controller;

import com.vendora.epic5.dto.*;
import com.vendora.epic5.model.DeliveryStatus;
import com.vendora.epic5.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController("epic5AdminController")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DeliveryService deliveryService;

    public AdminController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // ── Admin delivery management ─────────────────────────────

    @PostMapping("/admin/deliveries")
    public ResponseEntity<DeliveryDTO> create(@RequestBody CreateDeliveryDTO dto) {
        return ResponseEntity.ok(deliveryService.createDelivery(dto));
    }

    @GetMapping("/admin/deliveries")
    public ResponseEntity<List<DeliveryDTO>> getAll() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @GetMapping("/admin/deliveries/{id}")
    public ResponseEntity<DeliveryDTO> getOne(@PathVariable String id) {
        return ResponseEntity.ok(deliveryService.getDelivery(id));
    }

    @GetMapping("/admin/deliveries/status/{status}")
    public ResponseEntity<List<DeliveryDTO>> getByStatus(@PathVariable DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
    }

    /**
     * Assign an agent to a delivery.
     * The request body must include agentServiceDistrict so the service can
     * validate it matches the delivery's customerDistrict (sourced from the User module).
     */
    @PostMapping("/admin/deliveries/{id}/assign")
    public ResponseEntity<DeliveryAssignmentDTO> assign(
            @PathVariable String id,
            @RequestBody AssignAgentDTO dto) {
        return ResponseEntity.ok(deliveryService.assignAgent(id, dto));
    }

    @GetMapping("/admin/deliveries/{id}/history")
    public ResponseEntity<List<DeliveryStatusHistoryDTO>> getHistory(@PathVariable String id) {
        return ResponseEntity.ok(deliveryService.getStatusHistory(id));
    }

    // ── Agent lookup ──────────────────────────────────────────
    // Returns agent IDs sourced from assignment history in this module.
    // Full agent profiles (name, phone) live in the User Module.

    @GetMapping("/admin/agents")
    public ResponseEntity<List<Long>> getAgents() {
        return ResponseEntity.ok(deliveryService.getAllAgentIds());
    }

    @GetMapping("/admin/agents/by-district/{district}")
    public ResponseEntity<List<Long>> getAgentsByDistrict(@PathVariable String district) {
        return ResponseEntity.ok(deliveryService.getAgentIdsByDistrict(district));
    }

    // ── Return requests ────────────────────────────────────────

    @GetMapping("/admin/return-requests")
    public ResponseEntity<List<ReturnRequestDTO>> getAllReturns() {
        return ResponseEntity.ok(deliveryService.getAllReturnRequests());
    }

    @PostMapping("/admin/return-requests/{id}/approve")
    public ResponseEntity<ReturnRequestDTO> approveReturn(
            @PathVariable String id,
            @RequestParam(required = false) Long agentId,
            @RequestParam Long adminId) {
        return ResponseEntity.ok(deliveryService.approveReturn(id, agentId, adminId));
    }

    @PostMapping("/admin/return-requests/{id}/reject")
    public ResponseEntity<ReturnRequestDTO> rejectReturn(@PathVariable String id) {
        return ResponseEntity.ok(deliveryService.rejectReturn(id));
    }
}

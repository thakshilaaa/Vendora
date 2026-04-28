package com.vendora.epic5.controller;

import com.vendora.epic1.service.UserService;
import com.vendora.epic5.dto.DeliveryAssignmentDTO;
import com.vendora.epic5.dto.DeliveryDTO;
import com.vendora.epic5.dto.FailureLogRequestDTO;
import com.vendora.epic5.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/deliveryagent")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
public class DeliveryAgentController {

    private final DeliveryService deliveryService;
    private final UserService userService;

    public DeliveryAgentController(DeliveryService deliveryService, UserService userService) {
        this.deliveryService = deliveryService;
        this.userService = userService;
    }

    @GetMapping("/deliveries/{id}")
    public ResponseEntity<DeliveryDTO> getDeliveryForAgent(@PathVariable String id) {
        Long me = userService.getCurrentUser().getId();
        return ResponseEntity.ok(deliveryService.getDeliveryForAgent(id, me));
    }

    @GetMapping("/assignments/me")
    public ResponseEntity<List<DeliveryAssignmentDTO>> getMyAssignments() {
        Long agentId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(deliveryService.getAgentAssignments(agentId));
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<DeliveryAssignmentDTO>> getAssignments(@RequestParam Long agentId) {
        requireAgentSelf(agentId);
        return ResponseEntity.ok(deliveryService.getAgentAssignments(agentId));
    }

    @PostMapping("/assignments/{id}/accept")
    public ResponseEntity<DeliveryAssignmentDTO> accept(
            @PathVariable String id,
            @RequestParam Long agentId) {
        requireAgentSelf(agentId);
        return ResponseEntity.ok(deliveryService.acceptAssignment(id, agentId));
    }

    @PostMapping("/assignments/{id}/reject")
    public ResponseEntity<DeliveryAssignmentDTO> reject(
            @PathVariable String id,
            @RequestParam Long agentId,
            @RequestParam String reason) {
        requireAgentSelf(agentId);
        return ResponseEntity.ok(deliveryService.rejectAssignment(id, agentId, reason));
    }

    @PostMapping("/deliveries/{id}/pickup")
    public ResponseEntity<DeliveryDTO> pickup(
            @PathVariable String id,
            @RequestParam Long agentId) {
        requireAgentSelf(agentId);
        return ResponseEntity.ok(deliveryService.pickupDelivery(id, agentId));
    }

    @PostMapping("/deliveries/{id}/complete")
    public ResponseEntity<DeliveryDTO> complete(
            @PathVariable String id,
            @RequestParam Long agentId) {
        requireAgentSelf(agentId);
        return ResponseEntity.ok(deliveryService.completeDelivery(id, agentId));
    }

    @PostMapping("/deliveries/{id}/fail")
    public ResponseEntity<DeliveryDTO> fail(
            @PathVariable String id,
            @RequestBody FailureLogRequestDTO dto) {
        if (dto.getLoggedBy() == null) {
            dto.setLoggedBy(userService.getCurrentUser().getId());
        } else {
            requireAgentSelf(dto.getLoggedBy());
        }
        return ResponseEntity.ok(deliveryService.failDelivery(id, dto));
    }

    @PostMapping("/deliveries/{id}/pickup-return")
    public ResponseEntity<DeliveryDTO> pickupReturn(
            @PathVariable String id,
            @RequestParam Long agentId) {
        requireAgentSelf(agentId);
        return ResponseEntity.ok(deliveryService.pickupReturn(id, agentId));
    }

    @PostMapping("/deliveries/{id}/complete-return")
    public ResponseEntity<DeliveryDTO> completeReturn(
            @PathVariable String id,
            @RequestParam Long agentId) {
        requireAgentSelf(agentId);
        return ResponseEntity.ok(deliveryService.completeReturn(id, agentId));
    }

    private void requireAgentSelf(Long agentId) {
        if (agentId == null || !userService.getCurrentUser().getId().equals(agentId)) {
            throw new AccessDeniedException("Not this agent's action");
        }
    }
}

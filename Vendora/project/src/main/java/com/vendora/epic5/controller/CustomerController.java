package com.vendora.epic5.controller;

import com.vendora.epic1.service.UserService;
import com.vendora.epic5.dto.*;
import com.vendora.epic5.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController("epic5CustomerController")
@RequestMapping("/customer")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final DeliveryService deliveryService;
    private final UserService userService;

    public CustomerController(DeliveryService deliveryService, UserService userService) {
        this.deliveryService = deliveryService;
        this.userService = userService;
    }

    @GetMapping("/deliveries/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DeliveryDTO>> getMyDeliveriesForCurrentUser() {
        return ResponseEntity.ok(
                deliveryService.getCustomerDeliveries(userService.getCurrentUser().getId()));
    }

    @GetMapping("/deliveries")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DeliveryDTO>> getMyDeliveries(@RequestParam Long customerId) {
        if (!userService.getCurrentUser().getId().equals(customerId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(deliveryService.getCustomerDeliveries(customerId));
    }

    @GetMapping("/deliveries/{id}/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DeliveryStatusHistoryDTO>> getHistory(@PathVariable String id) {
        return ResponseEntity.ok(deliveryService.getStatusHistory(id));
    }

    @PostMapping("/deliveries/{id}/return-requests")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReturnRequestDTO> requestReturn(
            @PathVariable String id,
            @RequestBody ReturnRequestCreateDTO dto) {
        return ResponseEntity.ok(deliveryService.createReturnRequest(id, dto));
    }

    @GetMapping("/return-requests")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReturnRequestDTO>> getMyReturnRequests(@RequestParam Long customerId) {
        if (!userService.getCurrentUser().getId().equals(customerId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(deliveryService.getCustomerReturnRequests(customerId));
    }
}

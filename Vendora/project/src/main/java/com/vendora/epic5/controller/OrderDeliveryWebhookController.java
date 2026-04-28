package com.vendora.epic5.controller;

import com.vendora.epic5.dto.DeliveryDTO;
import com.vendora.epic5.dto.OrderPaymentDTO;
import com.vendora.epic5.service.DeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * HTTP entry for creating a delivery when payment is confirmed (e.g. external integrations).
 * In-app flow uses {@link com.vendora.epic4.service.OrderDeliverySyncService} directly.
 */
@RestController
@CrossOrigin(origins = "*")
public class OrderDeliveryWebhookController {

    private final DeliveryService deliveryService;

    public OrderDeliveryWebhookController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("/orders/payment-confirmed")
    public ResponseEntity<DeliveryDTO> createDeliveryFromOrder(@RequestBody OrderPaymentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryService.createDeliveryFromOrder(dto));
    }
}

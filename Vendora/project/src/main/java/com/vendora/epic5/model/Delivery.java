package com.vendora.epic5.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id = UUID.randomUUID().toString();

    /** FK → orders.order_id (BIGINT) */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /** FK → users.user_id (BIGINT) */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /** Cached from users.district at creation — used for district-based agent assignment */
    @Column(name = "customer_district", length = 50, nullable = false)
    private String customerDistrict;

    /** FK → users.user_id (BIGINT) — set when an agent is assigned */
    @Column(name = "agent_id")
    private Long agentId;

    @Column(name = "tracking_number", length = 50, nullable = false, unique = true)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @Column(name = "delivery_address", columnDefinition = "TEXT", nullable = false)
    private String deliveryAddress;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
}

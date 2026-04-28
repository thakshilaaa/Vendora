package com.vendora.epic5.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "return_requests")
public class ReturnRequest {

    @Id
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "delivery_id", columnDefinition = "CHAR(36)", nullable = false)
    private String deliveryId;

    /** FK → users.user_id (BIGINT) */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /** FK → users.user_id (BIGINT); set when admin assigns an agent to handle the return */
    @Column(name = "agent_id")
    private Long agentId;

    @Column(name = "reason_code", length = 50, nullable = false)
    private String reasonCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnRequestStatus status = ReturnRequestStatus.REQUESTED;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}

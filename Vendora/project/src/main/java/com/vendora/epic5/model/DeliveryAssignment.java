package com.vendora.epic5.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "delivery_assignments")
public class DeliveryAssignment {

    @Id
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "delivery_id", columnDefinition = "CHAR(36)", nullable = false)
    private String deliveryId;

    /** FK → users.user_id (BIGINT) */
    @Column(name = "agent_id", nullable = false)
    private Long agentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status = AssignmentStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
}

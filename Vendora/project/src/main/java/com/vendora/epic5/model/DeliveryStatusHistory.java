package com.vendora.epic5.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "delivery_status_history")
public class DeliveryStatusHistory {

    @Id
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "delivery_id", columnDefinition = "CHAR(36)", nullable = false)
    private String deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    /** FK → users.user_id (BIGINT); nullable for system-triggered transitions */
    @Column(name = "changed_by")
    private Long changedBy;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();
}

package com.vendora.epic5.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "failure_logs")
public class FailureLog {

    @Id
    @Column(columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "delivery_id", columnDefinition = "CHAR(36)", nullable = false)
    private String deliveryId;

    /** FK → users.user_id (BIGINT) — the agent who logged the failure */
    @Column(name = "logged_by", nullable = false)
    private Long loggedBy;

    @Column(name = "reason_code", length = 50, nullable = false)
    private String reasonCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "attempt_number", nullable = false)
    private int attemptNumber = 1;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt = LocalDateTime.now();
}

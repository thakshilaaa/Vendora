package com.vendora.epic5.dto;

import com.vendora.epic5.model.DeliveryAssignment;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeliveryAssignmentDTO {

    private String id;
    private String deliveryId;
    private Long agentId;
    private String status;
    private String rejectionReason;
    private LocalDateTime assignedAt;
    private LocalDateTime respondedAt;

    public static DeliveryAssignmentDTO from(DeliveryAssignment a) {
        DeliveryAssignmentDTO dto = new DeliveryAssignmentDTO();
        dto.setId(a.getId());
        dto.setDeliveryId(a.getDeliveryId());
        dto.setAgentId(a.getAgentId());
        dto.setStatus(a.getStatus().name());
        dto.setRejectionReason(a.getRejectionReason());
        dto.setAssignedAt(a.getAssignedAt());
        dto.setRespondedAt(a.getRespondedAt());
        return dto;
    }
}

package com.vendora.epic5.dto;

import com.vendora.epic5.model.ReturnRequest;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReturnRequestDTO {

    private String id;
    private String deliveryId;
    private Long customerId;
    private Long agentId;
    private String reasonCode;
    private String description;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

    public static ReturnRequestDTO from(ReturnRequest r) {
        ReturnRequestDTO dto = new ReturnRequestDTO();
        dto.setId(r.getId());
        dto.setDeliveryId(r.getDeliveryId());
        dto.setCustomerId(r.getCustomerId());
        dto.setAgentId(r.getAgentId());
        dto.setReasonCode(r.getReasonCode());
        dto.setDescription(r.getDescription());
        dto.setStatus(r.getStatus().name());
        dto.setRequestedAt(r.getRequestedAt());
        dto.setCompletedAt(r.getCompletedAt());
        return dto;
    }
}

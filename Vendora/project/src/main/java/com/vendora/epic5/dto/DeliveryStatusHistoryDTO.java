package com.vendora.epic5.dto;

import com.vendora.epic5.model.DeliveryStatusHistory;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeliveryStatusHistoryDTO {

    private String id;
    private String deliveryId;
    private String status;
    /** FK → users.user_id; null when changed by the system (e.g. auto-created on payment) */
    private Long changedBy;
    private LocalDateTime changedAt;

    public static DeliveryStatusHistoryDTO from(DeliveryStatusHistory h) {
        DeliveryStatusHistoryDTO dto = new DeliveryStatusHistoryDTO();
        dto.setId(h.getId());
        dto.setDeliveryId(h.getDeliveryId());
        dto.setStatus(h.getStatus().name());
        dto.setChangedBy(h.getChangedBy());
        dto.setChangedAt(h.getChangedAt());
        return dto;
    }
}

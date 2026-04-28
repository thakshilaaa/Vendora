package com.vendora.epic5.repository;

import com.vendora.epic5.model.DeliveryStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeliveryStatusHistoryRepository extends JpaRepository<DeliveryStatusHistory, String> {
    List<DeliveryStatusHistory> findByDeliveryIdOrderByChangedAtAsc(String deliveryId);
}

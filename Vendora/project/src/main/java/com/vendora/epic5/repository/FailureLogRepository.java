package com.vendora.epic5.repository;

import com.vendora.epic5.model.FailureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FailureLogRepository extends JpaRepository<FailureLog, String> {
    List<FailureLog> findByDeliveryIdOrderByLoggedAtDesc(String deliveryId);
    int countByDeliveryId(String deliveryId);
}

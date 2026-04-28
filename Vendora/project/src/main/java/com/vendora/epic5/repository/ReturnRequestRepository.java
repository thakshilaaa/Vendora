package com.vendora.epic5.repository;

import com.vendora.epic5.model.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, String> {
    List<ReturnRequest> findByCustomerIdOrderByRequestedAtDesc(Long customerId);
    List<ReturnRequest> findAllByOrderByRequestedAtDesc();
    List<ReturnRequest> findByDeliveryId(String deliveryId);
}

package com.vendora.epic5.repository;

import com.vendora.epic5.model.Delivery;
import com.vendora.epic5.model.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {
    List<Delivery> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Delivery> findByStatusOrderByCreatedAtDesc(DeliveryStatus status);
    List<Delivery> findAllByOrderByCreatedAtDesc();
    Optional<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByCustomerDistrictIgnoreCase(String district);
}

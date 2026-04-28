package com.vendora.epic5.repository;

import com.vendora.epic5.model.AssignmentStatus;
import com.vendora.epic5.model.DeliveryAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, String> {
    List<DeliveryAssignment> findByAgentIdOrderByAssignedAtDesc(Long agentId);
    List<DeliveryAssignment> findByDeliveryId(String deliveryId);
    Optional<DeliveryAssignment> findByDeliveryIdAndStatus(String deliveryId, AssignmentStatus status);
    List<DeliveryAssignment> findByDeliveryIdIn(List<String> deliveryIds);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT da.agentId FROM DeliveryAssignment da ORDER BY da.agentId")
    List<Long> findAllDistinctAgentIds();
}

package com.vendora.epic5.service;

import com.vendora.epic5.dto.*;
import com.vendora.epic5.model.*;
import com.vendora.epic5.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryAssignmentRepository assignmentRepository;
    private final DeliveryStatusHistoryRepository historyRepository;
    private final FailureLogRepository failureLogRepository;
    private final ReturnRequestRepository returnRequestRepository;

    public DeliveryService(DeliveryRepository deliveryRepository,
                           DeliveryAssignmentRepository assignmentRepository,
                           DeliveryStatusHistoryRepository historyRepository,
                           FailureLogRepository failureLogRepository,
                           ReturnRequestRepository returnRequestRepository) {
        this.deliveryRepository      = deliveryRepository;
        this.assignmentRepository    = assignmentRepository;
        this.historyRepository       = historyRepository;
        this.failureLogRepository    = failureLogRepository;
        this.returnRequestRepository = returnRequestRepository;
    }

    // ── INTEGRATION ───────────────────────────────────────────
    // Called by the Order module when a payment succeeds

    @Transactional
    public DeliveryDTO createDeliveryFromOrder(OrderPaymentDTO dto) {
        deliveryRepository.findByOrderId(dto.getOrderId()).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Delivery already exists for order " + dto.getOrderId());
        });

        Delivery delivery = new Delivery();
        delivery.setOrderId(dto.getOrderId());
        delivery.setCustomerId(dto.getCustomerId());
        delivery.setCustomerDistrict(dto.getCustomerDistrict());
        delivery.setTrackingNumber(generateTrackingNumber(dto.getOrderId(), dto.getOrderCode()));
        delivery.setDeliveryAddress(dto.getDeliveryAddress());
        delivery.setNotes(dto.getNotes());

        Delivery saved = deliveryRepository.save(delivery);
        recordHistory(saved.getId(), DeliveryStatus.PENDING, null);
        return DeliveryDTO.from(saved);
    }

    // ── ADMIN ─────────────────────────────────────────────────

    @Transactional
    public DeliveryDTO createDelivery(CreateDeliveryDTO dto) {
        deliveryRepository.findByOrderId(dto.getOrderId()).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Delivery already exists for order " + dto.getOrderId());
        });

        Delivery delivery = new Delivery();
        delivery.setOrderId(dto.getOrderId());
        delivery.setCustomerId(dto.getCustomerId());
        delivery.setCustomerDistrict(dto.getCustomerDistrict());
        delivery.setTrackingNumber(
            dto.getTrackingNumber() != null ? dto.getTrackingNumber()
                : generateTrackingNumber(dto.getOrderId(), null)
        );
        delivery.setDeliveryAddress(dto.getDeliveryAddress());
        delivery.setNotes(dto.getNotes());
        Delivery saved = deliveryRepository.save(delivery);
        recordHistory(saved.getId(), DeliveryStatus.PENDING, dto.getCreatedBy());
        return DeliveryDTO.from(saved);
    }

    public List<DeliveryDTO> getAllDeliveries() {
        return deliveryRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(DeliveryDTO::from).collect(Collectors.toList());
    }

    public DeliveryDTO getDelivery(String id) {
        return DeliveryDTO.from(findDelivery(id));
    }

    /** Agent UI: only deliveries this agent is assigned to (or currently holds) may be read. */
    public DeliveryDTO getDeliveryForAgent(String deliveryId, Long agentId) {
        Delivery d = findDelivery(deliveryId);
        if (agentId.equals(d.getAgentId())) {
            return DeliveryDTO.from(d);
        }
        boolean linked = assignmentRepository.findByAgentIdOrderByAssignedAtDesc(agentId).stream()
                .anyMatch(a -> deliveryId.equals(a.getDeliveryId()));
        if (!linked) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to view this delivery");
        }
        return DeliveryDTO.from(d);
    }

    public List<DeliveryDTO> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream().map(DeliveryDTO::from).collect(Collectors.toList());
    }

    /**
     * Assigns a delivery agent to a delivery.
     * Validates that the agent's service district matches the customer's delivery district.
     */
    @Transactional
    public DeliveryAssignmentDTO assignAgent(String deliveryId, AssignAgentDTO dto) {
        Delivery delivery = findDelivery(deliveryId);

        if (delivery.getStatus() == DeliveryStatus.DELIVERED
                || delivery.getStatus() == DeliveryStatus.RETURNED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Cannot assign an agent to a delivery that is already " + delivery.getStatus());
        }

        // District validation — reject if districts don't match
        String agentDistrict    = dto.getAgentServiceDistrict();
        String customerDistrict = delivery.getCustomerDistrict();
        if (agentDistrict != null && customerDistrict != null
                && !agentDistrict.equalsIgnoreCase(customerDistrict)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Agent service district '" + agentDistrict
                + "' does not match customer district '" + customerDistrict + "'");
        }

        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAgentId(dto.getAgentId());
        deliveryRepository.save(delivery);

        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setDeliveryId(deliveryId);
        assignment.setAgentId(dto.getAgentId());
        DeliveryAssignment saved = assignmentRepository.save(assignment);

        recordHistory(deliveryId, DeliveryStatus.ASSIGNED, dto.getAssignedBy());
        return DeliveryAssignmentDTO.from(saved);
    }

    public List<DeliveryStatusHistoryDTO> getStatusHistory(String deliveryId) {
        return historyRepository.findByDeliveryIdOrderByChangedAtAsc(deliveryId)
                .stream().map(DeliveryStatusHistoryDTO::from).collect(Collectors.toList());
    }

    public List<ReturnRequestDTO> getAllReturnRequests() {
        return returnRequestRepository.findAllByOrderByRequestedAtDesc()
                .stream().map(ReturnRequestDTO::from).collect(Collectors.toList());
    }

    @Transactional
    public ReturnRequestDTO approveReturn(String returnId, Long agentId, Long adminId) {
        ReturnRequest rr = findReturnRequest(returnId);
        if (rr.getStatus() != ReturnRequestStatus.REQUESTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Only REQUESTED returns can be approved, current status: " + rr.getStatus());
        }
        rr.setAgentId(agentId);
        rr.setStatus(agentId != null ? ReturnRequestStatus.PICKUP_SCHEDULED : ReturnRequestStatus.APPROVED);
        return ReturnRequestDTO.from(returnRequestRepository.save(rr));
    }

    @Transactional
    public ReturnRequestDTO rejectReturn(String returnId) {
        ReturnRequest rr = findReturnRequest(returnId);
        if (rr.getStatus() != ReturnRequestStatus.REQUESTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Only REQUESTED returns can be rejected, current status: " + rr.getStatus());
        }
        rr.setStatus(ReturnRequestStatus.REJECTED);
        return ReturnRequestDTO.from(returnRequestRepository.save(rr));
    }

    // ── ADMIN — AGENT LOOKUP ─────────────────────────────────

    /** All distinct agent IDs that have ever been assigned a delivery. */
    public List<Long> getAllAgentIds() {
        return assignmentRepository.findAllDistinctAgentIds();
    }

    /** Distinct agent IDs that have handled deliveries in the given district. */
    public List<Long> getAgentIdsByDistrict(String district) {
        List<String> deliveryIds = deliveryRepository.findByCustomerDistrictIgnoreCase(district)
                .stream().map(Delivery::getId).collect(Collectors.toList());
        if (deliveryIds.isEmpty()) return List.of();
        return assignmentRepository.findByDeliveryIdIn(deliveryIds)
                .stream().map(DeliveryAssignment::getAgentId)
                .distinct().sorted().collect(Collectors.toList());
    }

    // ── AGENT ─────────────────────────────────────────────────

    public List<DeliveryAssignmentDTO> getAgentAssignments(Long agentId) {
        return assignmentRepository.findByAgentIdOrderByAssignedAtDesc(agentId)
                .stream().map(DeliveryAssignmentDTO::from).collect(Collectors.toList());
    }

    @Transactional
    public DeliveryAssignmentDTO acceptAssignment(String assignmentId, Long agentId) {
        DeliveryAssignment assignment = findAssignment(assignmentId);
        if (!assignment.getAgentId().equals(agentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Agent is not assigned to this delivery");
        }
        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Assignment is not in PENDING status");
        }
        assignment.setStatus(AssignmentStatus.ACCEPTED);
        assignment.setRespondedAt(LocalDateTime.now());
        return DeliveryAssignmentDTO.from(assignmentRepository.save(assignment));
    }

    @Transactional
    public DeliveryAssignmentDTO rejectAssignment(String assignmentId, Long agentId, String reason) {
        DeliveryAssignment assignment = findAssignment(assignmentId);
        if (!assignment.getAgentId().equals(agentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Agent is not assigned to this delivery");
        }
        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Assignment is not in PENDING status");
        }
        assignment.setStatus(AssignmentStatus.REJECTED);
        assignment.setRejectionReason(reason);
        assignment.setRespondedAt(LocalDateTime.now());
        assignmentRepository.save(assignment);

        Delivery delivery = findDelivery(assignment.getDeliveryId());
        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.setAgentId(null);
        deliveryRepository.save(delivery);

        recordHistory(delivery.getId(), DeliveryStatus.PENDING, agentId);
        return DeliveryAssignmentDTO.from(assignment);
    }

    @Transactional
    public DeliveryDTO pickupDelivery(String deliveryId, Long agentId) {
        Delivery delivery = findDelivery(deliveryId);
        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        delivery.setPickedUpAt(LocalDateTime.now());
        deliveryRepository.save(delivery);
        recordHistory(deliveryId, DeliveryStatus.OUT_FOR_DELIVERY, agentId);
        return DeliveryDTO.from(delivery);
    }

    @Transactional
    public DeliveryDTO completeDelivery(String deliveryId, Long agentId) {
        Delivery delivery = findDelivery(deliveryId);
        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setDeliveredAt(LocalDateTime.now());
        deliveryRepository.save(delivery);
        recordHistory(deliveryId, DeliveryStatus.DELIVERED, agentId);
        return DeliveryDTO.from(delivery);
    }

    @Transactional
    public DeliveryDTO failDelivery(String deliveryId, FailureLogRequestDTO dto) {
        Delivery delivery = findDelivery(deliveryId);
        delivery.setStatus(DeliveryStatus.FAILED);
        deliveryRepository.save(delivery);

        int attemptNumber = failureLogRepository.countByDeliveryId(deliveryId) + 1;
        FailureLog log = new FailureLog();
        log.setDeliveryId(deliveryId);
        log.setLoggedBy(dto.getLoggedBy());
        log.setReasonCode(dto.getReasonCode());
        log.setDescription(dto.getDescription());
        log.setAttemptNumber(attemptNumber);
        failureLogRepository.save(log);

        recordHistory(deliveryId, DeliveryStatus.FAILED, dto.getLoggedBy());
        return DeliveryDTO.from(delivery);
    }

    @Transactional
    public DeliveryDTO pickupReturn(String deliveryId, Long agentId) {
        ReturnRequest rr = returnRequestRepository.findByDeliveryId(deliveryId)
                .stream()
                .filter(r -> r.getStatus() == ReturnRequestStatus.PICKUP_SCHEDULED)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No scheduled return for this delivery"));
        rr.setStatus(ReturnRequestStatus.PICKED_UP);
        returnRequestRepository.save(rr);
        return DeliveryDTO.from(findDelivery(deliveryId));
    }

    @Transactional
    public DeliveryDTO completeReturn(String deliveryId, Long agentId) {
        ReturnRequest rr = returnRequestRepository.findByDeliveryId(deliveryId)
                .stream()
                .filter(r -> r.getStatus() == ReturnRequestStatus.PICKED_UP)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No picked-up return for this delivery"));
        rr.setStatus(ReturnRequestStatus.COMPLETED);
        rr.setCompletedAt(LocalDateTime.now());
        returnRequestRepository.save(rr);

        Delivery delivery = findDelivery(deliveryId);
        delivery.setStatus(DeliveryStatus.RETURNED);
        deliveryRepository.save(delivery);
        recordHistory(deliveryId, DeliveryStatus.RETURNED, agentId);
        return DeliveryDTO.from(delivery);
    }

    // ── CUSTOMER ─────────────────────────────────────────────

    public List<DeliveryDTO> getCustomerDeliveries(Long customerId) {
        return deliveryRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream().map(DeliveryDTO::from).collect(Collectors.toList());
    }

    @Transactional
    public ReturnRequestDTO createReturnRequest(String deliveryId, ReturnRequestCreateDTO dto) {
        Delivery delivery = findDelivery(deliveryId);

        if (delivery.getStatus() != DeliveryStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Returns can only be requested for DELIVERED items, current status: " + delivery.getStatus());
        }
        if (!delivery.getCustomerId().equals(dto.getCustomerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Delivery does not belong to this customer");
        }

        ReturnRequest rr = new ReturnRequest();
        rr.setDeliveryId(deliveryId);
        rr.setCustomerId(dto.getCustomerId());
        rr.setReasonCode(dto.getReasonCode());
        rr.setDescription(dto.getDescription());
        return ReturnRequestDTO.from(returnRequestRepository.save(rr));
    }

    public List<ReturnRequestDTO> getCustomerReturnRequests(Long customerId) {
        return returnRequestRepository.findByCustomerIdOrderByRequestedAtDesc(customerId)
                .stream().map(ReturnRequestDTO::from).collect(Collectors.toList());
    }

    // ── HELPERS ───────────────────────────────────────────────

    private Delivery findDelivery(String id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found"));
    }

    private DeliveryAssignment findAssignment(String id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
    }

    private ReturnRequest findReturnRequest(String id) {
        return returnRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Return request not found"));
    }

    private void recordHistory(String deliveryId, DeliveryStatus status, Long changedBy) {
        DeliveryStatusHistory history = new DeliveryStatusHistory();
        history.setDeliveryId(deliveryId);
        history.setStatus(status);
        history.setChangedBy(changedBy);
        historyRepository.save(history);
    }

    /** Generates a unique tracking number: VND-{orderId}-{6-char UUID suffix} */
    private String generateTrackingNumber(Long orderId, String orderCode) {
        String base = (orderCode != null && !orderCode.isBlank())
            ? orderCode
            : String.format("%08d", orderId);
        return "VND-" + base + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}

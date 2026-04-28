package com.vendora.epic4.service;

import com.vendora.epic1.repository.UserRepository;
import com.vendora.epic4.model.Order;
import com.vendora.epic5.dto.OrderPaymentDTO;
import com.vendora.epic5.service.DeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * When an order is marked paid, create a delivery record (epic5) for linked customers.
 */
@Service
public class OrderDeliverySyncService {

    private final UserRepository userRepository;
    private final DeliveryService deliveryService;

    public OrderDeliverySyncService(UserRepository userRepository,
                                    DeliveryService deliveryService) {
        this.userRepository = userRepository;
        this.deliveryService = deliveryService;
    }

    public void tryCreateDeliveryForOrder(Order order) {
        if (order.getUserId() == null) {
            return;
        }
        userRepository.findById(order.getUserId()).ifPresent(user -> {
            OrderPaymentDTO dto = new OrderPaymentDTO();
            dto.setOrderId(order.getId());
            dto.setOrderCode("ORD-" + order.getId());
            dto.setCustomerId(user.getId());
            dto.setCustomerDistrict(user.getDistrict() != null ? user.getDistrict().name() : "UNKNOWN");
            dto.setDeliveryAddress(order.getProduct() != null ? order.getProduct() : "See order #");
            dto.setNotes(null);
            try {
                deliveryService.createDeliveryFromOrder(dto);
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode() != HttpStatus.CONFLICT) {
                    throw ex;
                }
            }
        });
    }
}

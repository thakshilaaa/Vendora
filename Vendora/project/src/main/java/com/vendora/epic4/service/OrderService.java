package com.vendora.epic4.service;

import com.vendora.epic3.dto.CheckoutRequest;
import com.vendora.epic3.model.CartItem;
import com.vendora.epic3.repository.CartItemRepository;
import com.vendora.epic3.service.DeliveryChargeService;
import com.vendora.epic4.model.Order;
import com.vendora.epic4.repository.OrderRepository;
import com.vendora.epic1.exception.ResourceNotFoundException;
import com.vendora.epic2.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private com.vendora.epic1.repository.UserRepository userRepo;

    @Autowired
    private CartItemRepository cartRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private DeliveryChargeService deliveryService;

    @Transactional
    public Order checkout(CheckoutRequest request, Long userId) {
        com.vendora.epic1.model.User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<CartItem> items = cartRepo.findAllById(request.getItemIds());
        for (CartItem item : items) {
            if (item.getCart() == null || item.getCart().getUser() == null
                    || !item.getCart().getUser().getId().equals(userId)) {
                throw new com.vendora.epic1.exception.ForbiddenException("Invalid cart items for this user");
            }
        }

        StringBuilder productSummary = new StringBuilder();
        productSummary.append("[Address: ").append(request.getAddress()).append(", ")
                      .append(request.getLocationType().name()).append("] ");

        double total = 0;
        for (CartItem item : items) {
            String name = item.getProduct().getName();
            int qty = item.getQuantity();
            double price = item.getProduct().getPrice().doubleValue();

            productSummary.append(name)
                          .append("(x").append(qty).append(")-")
                          .append(price * qty)
                          .append(", ");

            total += price * qty;

            // reduce stock
            Product p = item.getProduct();
            p.setStockQuantity(p.getStockQuantity() - qty);
        }

        double deliveryFee = deliveryService.calculate(request.getLocationType()).doubleValue();
        total += deliveryFee;

        Order order = new Order();
        String fullName = user.getFullName();
        String[] nameParts = fullName != null ? fullName.trim().split("\\s+", 2) : new String[]{"", ""};
        order.setFirstName(nameParts.length > 0 ? nameParts[0] : "");
        order.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        order.setProduct(productSummary.toString());
        order.setAmount(total);
        order.setStatus("PENDING");
        order.setPaymentStatus("UNPAID");
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPhone(user.getPhone());
        order.setUserId(userId);

        Order savedOrder = orderRepo.save(order);

        // clear cart
        cartRepo.deleteAll(items);
        
        return savedOrder;
    }
}

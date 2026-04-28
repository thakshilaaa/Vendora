package com.vendora.epic4.controller;

import com.vendora.epic1.exception.UnauthorizedException;
import com.vendora.epic1.service.UserService;
import com.vendora.epic4.model.Order;
import com.vendora.epic4.model.Payment;
import com.vendora.epic4.repository.OrderRepository;
import com.vendora.epic4.repository.PaymentRepository;
import com.vendora.epic4.service.OrderDeliverySyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final OrderDeliverySyncService orderDeliverySyncService;

    public OrderController(OrderRepository orderRepository,
                           PaymentRepository paymentRepository,
                           UserService userService,
                           OrderDeliverySyncService orderDeliverySyncService) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.orderDeliverySyncService = orderDeliverySyncService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public List<Order> getMyOrders() {
        return orderRepository.findByUserIdOrderByIdDesc(userService.getCurrentUser().getId());
    }

    @PostMapping("/add")
    public Order addOrder(@RequestBody Order order) {
        if (order.getStatus() == null) {
            order.setStatus("PENDING");
        }
        if (order.getPaymentStatus() == null) {
            order.setPaymentStatus("UNPAID");
        }
        try {
            order.setUserId(userService.getCurrentUser().getId());
        } catch (UnauthorizedException ignored) {
        }
        return orderRepository.save(order);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        if (!isCurrentUserAdmin() && order.getUserId() != null) {
            try {
                if (!order.getUserId().equals(userService.getCurrentUser().getId())) {
                    return ResponseEntity.status(403).body("Not your order");
                }
            } catch (UnauthorizedException e) {
                return ResponseEntity.status(401).body("Sign in to cancel this order");
            }
        }

        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            order.setStatus("Refund Requested");
            orderRepository.save(order);
            return ResponseEntity.ok("Refund Request Submitted.");
        }

        order.setStatus("Cancelled");
        orderRepository.save(order);
        return ResponseEntity.ok("Order Cancelled.");
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestBody String status) {
        return orderRepository.findById(id).map(o -> {
            String cleanStatus = status.replace("\"", "");
            o.setStatus(cleanStatus);
            return ResponseEntity.ok(orderRepository.save(o));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/pay-confirm")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> confirmPayment(@PathVariable Long id) {
        return orderRepository.findById(id).map(order -> {
            order.setPaymentStatus("PAID");
            orderRepository.save(order);
            List<Payment> payments = paymentRepository.findByOrderId(id);
            if (payments != null) {
                for (Payment p : payments) {
                    p.setStatus("PAID");
                    paymentRepository.save(p);
                }
            }
            orderDeliverySyncService.tryCreateDeliveryForOrder(order);
            return ResponseEntity.ok(order);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        return orderRepository.findById(id).map(order -> {
            orderRepository.delete(order);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus("PENDING");
        Double revenue = orderRepository.getTotalRevenue();
        Double avgValue = orderRepository.getAverageOrderValue();

        stats.put("totalRevenue", revenue != null ? String.format("%.2f", revenue) : "0.00");
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("avgOrderValue", avgValue != null ? String.format("%.2f", avgValue) : "0.00");
        return ResponseEntity.ok(stats);
    }

    private static boolean isCurrentUserAdmin() {
        var a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || "anonymousUser".equals(a.getPrincipal())) {
            return false;
        }
        return a.getAuthorities().stream()
                .anyMatch(ga -> "ROLE_ADMIN".equals(ga.getAuthority()));
    }
}

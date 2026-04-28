package com.vendora.epic4.controller;

import com.vendora.epic1.exception.ForbiddenException;
import com.vendora.epic1.exception.ResourceNotFoundException;
import com.vendora.epic1.model.User;
import com.vendora.epic1.service.UserService;
import com.vendora.epic4.model.Order;
import com.vendora.epic4.repository.OrderRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CustomerOrderViewController {

    private final OrderRepository orderRepository;
    private final UserService userService;

    public CustomerOrderViewController(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    @GetMapping("/orders")
    public String orderHistory() {
        return "epic4/html/order_history";
    }

    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getUserId() != null) {
            if (!isCurrentUserAdmin()) {
                var a = SecurityContextHolder.getContext().getAuthentication();
                if (a == null || !a.isAuthenticated() || "anonymousUser".equals(a.getPrincipal())) {
                    return "redirect:/login";
                }
                User u = userService.getCurrentUser();
                if (!order.getUserId().equals(u.getId())) {
                    throw new ForbiddenException("Not your order");
                }
            }
        }
        model.addAttribute("order", order);
        return "epic4/html/order_detail";
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

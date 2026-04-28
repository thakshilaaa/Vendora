package com.vendora.epic1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "redirect:/customer-dashboard";
    }

    // ── Static HTML pages ──────────────────────────────────────────────
    @GetMapping("/about")
    public String about() { return "forward:/html/about.html"; }

    @GetMapping("/cookie")
    public String cookie() { return "forward:/html/cookie.html"; }

    @GetMapping("/faq")
    public String faq() { return "forward:/html/faq.html"; }

    @GetMapping("/feedback")
    public String feedback() { return "forward:/html/feedback.html"; }

    @GetMapping("/privacy")
    public String privacy() { return "forward:/html/privacy.html"; }

    @GetMapping("/routine")
    public String routine() { return "forward:/html/routine.html"; }

    @GetMapping("/service")
    public String service() { return "forward:/html/service.html"; }

    @GetMapping("/terms")
    public String terms() { return "forward:/html/terms.html"; }

    // ── Epic 1 – Auth / Admin / Registration ───────────────────────────
    @GetMapping("/admin-login")
    public String adminLogin() { return "epic1/admin/admin-login"; }

    @GetMapping("/pending-registrations")
    public String pendingRegistrations() { return "epic1/admin/pending-registrations"; }

    @GetMapping("/users")
    public String users() { return "epic1/admin/users"; }

    @GetMapping("/forget-password")
    public String forgotPassword() { return "epic1/auth/forget-password"; }

    @GetMapping("/login")
    public String login() { return "epic1/auth/login"; }

    @GetMapping("/logout")
    public String logout() { return "epic1/auth/logout"; }

    @GetMapping("/profile")
    public String profile() { return "epic1/auth/profile"; }

    @GetMapping("/update-password")
    public String updatePassword() { return "epic1/auth/update-password"; }

    @GetMapping("/verify-email")
    public String verifyEmail() { return "epic1/auth/verify-email"; }

    @GetMapping("/admin-dashboard")
    public String adminDashboard() { return "epic1/dashboard/admin-dashboard"; }

    @GetMapping("/customer-dashboard")
    public String customerDashboard() { return "epic1/dashboard/customer-dashboard"; }

    @GetMapping("/delivery-dashboard")
    public String deliveryDashboard() { return "epic1/dashboard/delivery-dashboard"; }

    @GetMapping("/supplier-dashboard")
    public String supplierDashboard() { return "epic1/dashboard/supplier-dashboard"; }

    @GetMapping("/admin-signup")
    public String adminSignup() { return "epic1/registration/admin-signup"; }

    @GetMapping("/complete-registration")
    public String completeRegistration(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String token,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String email,
            org.springframework.ui.Model model) {
        model.addAttribute("token", token);
        model.addAttribute("email", email);
        return "epic1/registration/complete-registration";
    }

    @GetMapping("/customer-signup")
    public String customerSignup() { return "epic1/registration/customer-signup"; }

    @GetMapping("/delivery-register")
    public String deliveryRegister() { return "epic1/registration/delivery-register"; }

    @GetMapping("/supplier-register")
    public String supplierRegister() { return "epic1/registration/supplier-register"; }

    // ── Epic 2 – Products ──────────────────────────────────────────────
    @GetMapping("/products-admin")
    public String productsAdmin() { return "epic2/html/index"; }

    @GetMapping("/products")
    public String products() { return "epic2/html/products"; }

    @GetMapping("/create-product")
    public String createProduct() { return "epic2/html/create-product"; }

    @GetMapping("/edit-product")
    public String editProduct() { return "epic2/html/edit-product"; }

    @GetMapping("/low-stock")
    public String lowStock() { return "epic2/html/low-stock"; }

    @GetMapping("/product-detail")
    public String productDetail() { return "epic2/html/product-detail"; }

    @GetMapping("/shop")
    public String shop() { return "epic2/html/user-products"; }

    @GetMapping("/product-view")
    public String productView() { return "epic2/html/user-product-detail"; }

    // ── Epic 4 – Orders & Payments ─────────────────────────────────────
    @GetMapping("/order-admin")
    public String orderAdmin() { return "epic4/html/order_and_payment_admin"; }

    @GetMapping("/order-payment")
    public String orderPayment() { return "epic4/html/order_and_payment_index"; }

    @GetMapping("/order-history")
    public String orderHistory() { return "epic4/html/order_history"; }

    // ── Epic 5 – Delivery ──────────────────────────────────────────────
    @GetMapping("/delivery-admin")
    public String deliveryAdmin() { return "epic5/html/Admin"; }

    @GetMapping("/delivery-agent")
    public String deliveryAgent() { return "epic5/html/Agent"; }

    @GetMapping("/delivery-customer")
    public String deliveryCustomer() { return "epic5/html/Customer"; }

    // ── Epic 6 – Supplier Management ───────────────────────────────────
    @GetMapping("/supplier-portal")
    public String supplierPortal() { return "epic1/dashboard/supplier-dashboard"; }

    @GetMapping("/supplier-approvals")
    public String supplierApprovals() { return "redirect:/admin/suppliers/pending"; }

    @GetMapping("/supplier-list")
    public String supplierList() { return "redirect:/admin/suppliers"; }
}

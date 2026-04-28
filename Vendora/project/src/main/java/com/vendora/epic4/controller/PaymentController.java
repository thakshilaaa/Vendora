package com.vendora.epic4.controller;

import com.vendora.epic4.model.Payment;
import com.vendora.epic4.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/process")
    public Payment processPayment(@RequestBody Payment payment) {
        if (payment.getTransactionId() == null || payment.getTransactionId().isEmpty()) {
            payment.setTransactionId("TXN-" + System.currentTimeMillis());
        }
        return paymentRepository.save(payment);
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Payment> getPaymentHistory() {
        return paymentRepository.findAll();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
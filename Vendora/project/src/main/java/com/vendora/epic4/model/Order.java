package com.vendora.epic4.model;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "orders")
@DynamicUpdate
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String product;
    private double amount;
    private String status;
    private String paymentStatus;
    private String phone;

    // Getter and Setter
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }


    // Default Constructor
    public Order() {}

    // All-Args Constructor
    public Order(Long id, String firstName, String lastName, String product, double amount, String status, String paymentStatus, String paymentMethod) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.product = product;
        this.amount = amount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }


    private String paymentMethod;

    /** Set by checkout; used to filter a customer's own orders. */
    @Column(name = "user_id")
    private Long userId;

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
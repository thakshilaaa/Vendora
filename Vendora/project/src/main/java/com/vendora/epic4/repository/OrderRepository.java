package com.vendora.epic4.repository;


import com.vendora.epic4.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    long count();


    long countByStatus(String status);

    @Query("SELECT SUM(o.amount) FROM Order o WHERE o.paymentStatus = 'PAID'")
    Double getTotalRevenue();

    @Query("SELECT AVG(o.amount) FROM Order o WHERE o.paymentStatus = 'PAID'")
    Double getAverageOrderValue();

    /** All pending platform orders (Order has no supplier line item yet; supplier dashboard uses this as a global count.) */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'")
    long countPendingOrdersAll();

    List<Order> findByUserIdOrderByIdDesc(Long userId);
}
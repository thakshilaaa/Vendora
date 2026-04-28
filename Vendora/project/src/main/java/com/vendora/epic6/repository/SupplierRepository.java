package com.vendora.epic6.repository;

import com.vendora.epic6.model.Supplier;
import com.vendora.epic6.model.SupplierStatus;
import com.vendora.epic1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByUser_Id(Long userId);

    Optional<Supplier> findByEmail(String email);
    List<Supplier> findByStatus(SupplierStatus status);
    List<Supplier> findByCompanyNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String q1, String q2);
    Optional<Supplier> findByUser(User user);
    List<Supplier> findByCompanyNameContainingIgnoreCase(String query);
}

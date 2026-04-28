package com.vendora.epic3.repository;

import com.vendora.epic3.model.Cart;
import com.vendora.epic1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}

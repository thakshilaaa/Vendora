package com.vendora.epic3.repository;

import com.vendora.epic3.model.Cart;
import com.vendora.epic3.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    void deleteByCart(Cart cart);
}

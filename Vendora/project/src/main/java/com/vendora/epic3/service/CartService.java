package com.vendora.epic3.service;

import com.vendora.epic3.dto.CartItemDto;
import com.vendora.epic3.model.Cart;
import com.vendora.epic3.model.CartItem;
import com.vendora.epic3.repository.CartItemRepository;
import com.vendora.epic3.repository.CartRepository;
import com.vendora.epic2.model.Product;
import com.vendora.epic1.exception.ForbiddenException;
import com.vendora.epic1.model.User;
import com.vendora.epic2.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Cart business logic. Wraps the team's Cart / CartItem / Product entities.
 *
 * NOTE: Adjust the imports above (com.vendora.model.*) to match the
 * package the team uses for the shared domain entities.
 */
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    /** Get (or lazily create) the cart belonging to this user. */
    @Transactional
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });
    }

    /** Map cart items to DTOs the views and JSON endpoint can consume. */
    @Transactional(readOnly = true)
    public List<CartItemDto> listItems(User user) {
        Cart cart = getOrCreateCart(user);
        List<CartItemDto> result = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            Product p = ci.getProduct();
            result.add(new CartItemDto(
                    ci.getId(),
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    ci.getQuantity(),
                    p.getStockQuantity(),
                    buildProductImageDataUrl(p)
            ));
        }
        return result;
    }

    @Transactional
    public void addToCart(User user, Long productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            int next = Math.min(existing.getQuantity() + quantity, product.getStockQuantity());
            existing.setQuantity(next);
            cartItemRepository.save(existing);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(Math.min(quantity, product.getStockQuantity()));
            cartItemRepository.save(item);
            cart.getItems().add(item);
        }
    }

    @Transactional
    public CartItemDto updateQuantity(User user, Long itemId, int newQuantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Not your cart item");
        }

        int stock = item.getProduct().getStockQuantity();
        int clamped = Math.max(1, Math.min(newQuantity, stock));
        item.setQuantity(clamped);
        cartItemRepository.save(item);

        Product p = item.getProduct();
        return new CartItemDto(
                item.getId(), p.getId(), p.getName(), p.getPrice(),
                item.getQuantity(), p.getStockQuantity(),
                buildProductImageDataUrl(p)
        );
    }

    private static String buildProductImageDataUrl(Product p) {
        if (p.getImage() == null || p.getImage().length == 0) {
            return "/images/Vendora.png";
        }
        String ct = p.getImageContentType() != null ? p.getImageContentType() : "image/jpeg";
        return "data:" + ct + ";base64," + Base64.getEncoder().encodeToString(p.getImage());
    }

    @Transactional
    public void removeItem(User user, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Not your cart item");
        }
        cartItemRepository.delete(item);
    }

    @Transactional
    public void removeItems(User user, List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) return;
        for (Long id : itemIds) {
            removeItem(user, id);
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal subtotalFor(User user, List<Long> itemIds) {
        return listItems(user).stream()
                .filter(i -> itemIds == null || itemIds.contains(i.getId()))
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

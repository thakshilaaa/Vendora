package com.vendora.epic3.dto;

import java.math.BigDecimal;

public class CartItemDto {

    private Long id;            // CartItem id
    private Long productId;
    private String name;
    private BigDecimal price;
    private int quantity;
    private int stock;
    private String imageUrl;

    public CartItemDto() {}

    public CartItemDto(Long id, Long productId, String name, BigDecimal price,
                       int quantity, int stock, String imageUrl) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    public BigDecimal getSubtotal() {
        return price == null ? BigDecimal.ZERO : price.multiply(BigDecimal.valueOf(quantity));
    }

    // ----- getters / setters -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

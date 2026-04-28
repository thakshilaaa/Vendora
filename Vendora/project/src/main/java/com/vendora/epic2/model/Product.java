package com.vendora.epic2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @NotNull(message = "Supplier is required")
    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false, length = 200)
    private String name;

    @NotBlank(message = "Brand is required")
    @Column(nullable = false, length = 100)
    private String brand;

    @Column(unique = true, length = 100)
    private String sku;

    @Column(unique = true, length = 50)
    private String barcode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String ingredients;

    @Column(name = "usage_instructions", columnDefinition = "TEXT")
    private String usageInstructions;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @NotNull(message = "Low stock threshold is required")
    @Min(value = 1)
    @Column(name = "low_stock_threshold", nullable = false)
    private Integer lowStockThreshold;

    @Column(length = 50)
    private String unit; // e.g. ml, g, oz, pcs

    @Column(name = "shade", length = 100)
    private String shade;

    @Column(name = "skin_type", length = 100)
    private String skinType; // e.g. Oily, Dry, Combination

    @Column(name = "volume", length = 100)
    private String volume; // e.g. 200ml, 50g

    // Populated in service for API/display — not DB columns
    @Transient
    private String supplierName;
    @Transient
    private String supplierContact;
    @Transient
    private String supplierEmail;
    @Transient
    private String supplierAddress;

    // Dates
    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    // Image stored directly in MySQL as LONGBLOB
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(name = "image_content_type", length = 50)
    private String imageContentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(name = "country_of_origin", length = 100)
    private String countryOfOrigin;

    @Column(columnDefinition = "TEXT")
    private String tags; // comma separated

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Computed: is this product low on stock?
    @Transient
    public boolean isLowStock() {
        return stockQuantity != null && lowStockThreshold != null
               && stockQuantity <= lowStockThreshold;
    }

    /**
     * Values must match the {@code products.category} ENUM in {@code Vendora - Database.sql}.
     */
    public enum Category {
        COSMETICS,
        SKINCARE,
        HAIRCARE,
        BODYCARE,
        FRAGRANCE,
        BEAUTY_TOOLS
    }

    public enum ProductStatus {
        ACTIVE, INACTIVE, DISCONTINUED
    }
}

package com.vendora.epic2.dto;

import com.vendora.epic2.model.Product;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProductDto {

    /**
     * Response DTO - used for list and detail views (image as Base64 string)
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Long supplierId;
        private String name;
        private String brand;
        private String sku;
        private String barcode;
        private Product.Category category;
        private String description;
        private String ingredients;
        private String usageInstructions;
        private BigDecimal price;
        private BigDecimal costPrice;
        private Integer stockQuantity;
        private Integer lowStockThreshold;
        private boolean lowStock;
        private String unit;
        private String shade;
        private String skinType;
        private String volume;
        private String supplierName;
        private String supplierContact;
        private String supplierEmail;
        private String supplierAddress;
        private LocalDate manufactureDate;
        private LocalDate expiryDate;
        private String imageBase64;
        private String imageContentType;
        private Product.ProductStatus status;
        private String countryOfOrigin;
        private String tags;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * Summary DTO for list view (no image blob - performance)
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private Long id;
        private Long supplierId;
        private String name;
        private String brand;
        private String sku;
        private String barcode;
        private Product.Category category;
        private BigDecimal price;
        private Integer stockQuantity;
        private Integer lowStockThreshold;
        private boolean lowStock;
        private String unit;
        private String shade;
        private Product.ProductStatus status;
        private String imageBase64;   // thumbnail
        private String imageContentType;
        private LocalDateTime createdAt;
    }

    /**
     * Stock update DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockUpdate {
        private Integer quantity;
        private String operation; // "ADD" | "SUBTRACT" | "SET"
    }

    /**
     * Dashboard stats DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardStats {
        private long totalProducts;
        private long activeProducts;
        private long lowStockCount;
        private long outOfStockCount;
        private long totalStockValue;
        private long categoryCount;
    }
}

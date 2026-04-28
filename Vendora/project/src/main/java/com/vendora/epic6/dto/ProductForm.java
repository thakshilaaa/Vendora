package com.vendora.epic6.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public class ProductForm {

    private Long id;                    // null when creating

    @NotBlank(message = "Name is required")
    @Size(max = 150)
    private String name;

    @NotBlank(message = "Category is required")
    private String category;            // Skincare / Makeup / Fragrance / Haircare / Bath & Body

    @NotNull @DecimalMin(value = "0.0", inclusive = false, message = "Price must be > 0")
    private BigDecimal price;           // LKR

    @NotNull @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;

    @Size(max = 2000)
    private String description;

    /** Optional — only required when creating. */
    private MultipartFile image;

    /** Existing image path (used on edit when no new image is uploaded). */
    private String existingImagePath;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public MultipartFile getImage() { return image; }
    public void setImage(MultipartFile image) { this.image = image; }
    public String getExistingImagePath() { return existingImagePath; }
    public void setExistingImagePath(String existingImagePath) { this.existingImagePath = existingImagePath; }
}

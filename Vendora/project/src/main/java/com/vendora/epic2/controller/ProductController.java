package com.vendora.epic2.controller;

import com.vendora.epic2.dto.ProductDto;
import com.vendora.epic2.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ProductController {

    private final ProductService productService;

    // ==================== GET ALL (with filters) ====================
    @GetMapping
    public ResponseEntity<List<ProductDto.Summary>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(productService.getAllProducts(keyword, category, status));
    }

    // ==================== GET ONE ====================
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto.Response> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // ==================== CREATE ====================
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ProductDto.Response> createProduct(
            @RequestParam Long supplierId,
            @RequestParam String name,
            @RequestParam String brand,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String barcode,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) String usageInstructions,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) BigDecimal costPrice,
            @RequestParam Integer stockQuantity,
            @RequestParam(defaultValue = "10") Integer lowStockThreshold,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String shade,
            @RequestParam(required = false) String skinType,
            @RequestParam(required = false) String volume,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String supplierContact,
            @RequestParam(required = false) String supplierEmail,
            @RequestParam(required = false) String supplierAddress,
            @RequestParam(required = false) String manufactureDate,
            @RequestParam(required = false) String expiryDate,
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(required = false) String countryOfOrigin,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) MultipartFile image) throws IOException {

        return ResponseEntity.status(201).body(productService.createProduct(
            supplierId,
            name, brand, sku, barcode, category, description, ingredients, usageInstructions,
            price, costPrice, stockQuantity, lowStockThreshold, unit, shade, skinType, volume,
            supplierName, supplierContact, supplierEmail, supplierAddress,
            manufactureDate, expiryDate, status, countryOfOrigin, tags, image
        ));
    }

    // ==================== UPDATE ====================
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ProductDto.Response> updateProduct(
            @PathVariable Long id,
            @RequestParam(required = false) Long supplierId,
            @RequestParam String name,
            @RequestParam String brand,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String barcode,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) String usageInstructions,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) BigDecimal costPrice,
            @RequestParam Integer stockQuantity,
            @RequestParam(defaultValue = "10") Integer lowStockThreshold,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String shade,
            @RequestParam(required = false) String skinType,
            @RequestParam(required = false) String volume,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String supplierContact,
            @RequestParam(required = false) String supplierEmail,
            @RequestParam(required = false) String supplierAddress,
            @RequestParam(required = false) String manufactureDate,
            @RequestParam(required = false) String expiryDate,
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(required = false) String countryOfOrigin,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(defaultValue = "false") boolean removeImage) throws IOException {

        return ResponseEntity.ok(productService.updateProduct(
            id, supplierId, name, brand, sku, barcode, category, description, ingredients, usageInstructions,
            price, costPrice, stockQuantity, lowStockThreshold, unit, shade, skinType, volume,
            supplierName, supplierContact, supplierEmail, supplierAddress,
            manufactureDate, expiryDate, status, countryOfOrigin, tags, image, removeImage
        ));
    }

    // ==================== DELETE ====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== STOCK ====================
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDto.Response> updateStock(
            @PathVariable Long id,
            @RequestBody ProductDto.StockUpdate stockUpdate) {
        return ResponseEntity.ok(productService.updateStock(id, stockUpdate));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDto.Summary>> getLowStockProducts() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }

    // ==================== DASHBOARD STATS ====================
    @GetMapping("/stats/dashboard")
    public ResponseEntity<ProductDto.DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(productService.getDashboardStats());
    }
}

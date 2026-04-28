package com.vendora.epic2.service;

import com.vendora.epic2.dto.ProductDto;
import com.vendora.epic1.exception.ResourceNotFoundException;
import com.vendora.epic2.model.Product;
import com.vendora.epic2.repository.ProductRepository;
import com.vendora.epic6.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    // ==================== CREATE ====================
    public ProductDto.Response createProduct(
            Long supplierId,
            String name, String brand, String sku, String barcode,
            String categoryStr, String description, String ingredients,
            String usageInstructions, BigDecimal price, BigDecimal costPrice,
            Integer stockQuantity, Integer lowStockThreshold, String unit,
            String shade, String skinType, String volume,
            String supplierName, String supplierContact, String supplierEmail, String supplierAddress,
            String manufactureDateStr, String expiryDateStr,
            String status, String countryOfOrigin, String tags,
            MultipartFile image) throws IOException {

        if (supplierId == null) {
            throw new IllegalArgumentException("supplierId is required");
        }

        // Validate unique fields
        if (sku != null && !sku.isBlank() && productRepository.existsBySku(sku)) {
            throw new IllegalArgumentException("SKU already exists: " + sku);
        }
        if (barcode != null && !barcode.isBlank() && productRepository.existsByBarcode(barcode)) {
            throw new IllegalArgumentException("Barcode already exists: " + barcode);
        }

        Product product = Product.builder()
            .supplierId(supplierId)
            .name(name)
            .brand(brand)
            .sku(sku)
            .barcode(barcode)
            .category(Product.Category.valueOf(categoryStr))
            .description(description)
            .ingredients(ingredients)
            .usageInstructions(usageInstructions)
            .price(price)
            .costPrice(costPrice)
            .stockQuantity(stockQuantity)
            .lowStockThreshold(lowStockThreshold != null ? lowStockThreshold : 10)
            .unit(unit)
            .shade(shade)
            .skinType(skinType)
            .volume(volume)
            .manufactureDate(manufactureDateStr != null && !manufactureDateStr.isBlank() ? LocalDate.parse(manufactureDateStr) : null)
            .expiryDate(expiryDateStr != null && !expiryDateStr.isBlank() ? LocalDate.parse(expiryDateStr) : null)
            .status(status != null ? Product.ProductStatus.valueOf(status) : Product.ProductStatus.ACTIVE)
            .countryOfOrigin(countryOfOrigin)
            .tags(tags)
            .build();

        product.setSupplierName(supplierName);
        product.setSupplierContact(supplierContact);
        product.setSupplierEmail(supplierEmail);
        product.setSupplierAddress(supplierAddress);
        applyTransientSupplierFromProfile(product);

        if (image != null && !image.isEmpty()) {
            product.setImage(image.getBytes());
            product.setImageContentType(image.getContentType());
        }

        return toResponse(productRepository.save(product));
    }

    // ==================== READ ALL ====================
    @Transactional(readOnly = true)
    public List<ProductDto.Summary> getAllProducts(String keyword, String category, String status) {
        List<Product> products;

        if (keyword != null && !keyword.isBlank()) {
            products = productRepository.searchProducts(keyword.trim());
        } else {
            products = productRepository.findAll();
        }

        return products.stream()
            .filter(p -> category == null || category.isBlank() || p.getCategory().name().equalsIgnoreCase(category))
            .filter(p -> status == null || status.isBlank() || p.getStatus().name().equalsIgnoreCase(status))
            .map(p -> { applyTransientSupplierFromProfile(p); return toSummary(p); })
            .collect(Collectors.toList());
    }

    // ==================== READ ONE ====================
    @Transactional(readOnly = true)
    public ProductDto.Response getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        applyTransientSupplierFromProfile(product);
        return toResponse(product);
    }

    // ==================== UPDATE ====================
    public ProductDto.Response updateProduct(
            Long id,
            Long supplierId,
            String name, String brand, String sku, String barcode,
            String categoryStr, String description, String ingredients,
            String usageInstructions, BigDecimal price, BigDecimal costPrice,
            Integer stockQuantity, Integer lowStockThreshold, String unit,
            String shade, String skinType, String volume,
            String supplierName, String supplierContact, String supplierEmail, String supplierAddress,
            String manufactureDateStr, String expiryDateStr,
            String status, String countryOfOrigin, String tags,
            MultipartFile image, boolean removeImage) throws IOException {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (sku != null && !sku.isBlank() && productRepository.existsBySkuAndIdNot(sku, id)) {
            throw new IllegalArgumentException("SKU already exists: " + sku);
        }
        if (barcode != null && !barcode.isBlank() && productRepository.existsByBarcodeAndIdNot(barcode, id)) {
            throw new IllegalArgumentException("Barcode already exists: " + barcode);
        }

        if (supplierId != null) {
            product.setSupplierId(supplierId);
        }
        product.setName(name);
        product.setBrand(brand);
        product.setSku(sku);
        product.setBarcode(barcode);
        product.setCategory(Product.Category.valueOf(categoryStr));
        product.setDescription(description);
        product.setIngredients(ingredients);
        product.setUsageInstructions(usageInstructions);
        product.setPrice(price);
        product.setCostPrice(costPrice);
        product.setStockQuantity(stockQuantity);
        product.setLowStockThreshold(lowStockThreshold != null ? lowStockThreshold : 10);
        product.setUnit(unit);
        product.setShade(shade);
        product.setSkinType(skinType);
        product.setVolume(volume);
        product.setSupplierName(supplierName);
        product.setSupplierContact(supplierContact);
        product.setSupplierEmail(supplierEmail);
        product.setSupplierAddress(supplierAddress);
        product.setManufactureDate(manufactureDateStr != null && !manufactureDateStr.isBlank() ? LocalDate.parse(manufactureDateStr) : null);
        product.setExpiryDate(expiryDateStr != null && !expiryDateStr.isBlank() ? LocalDate.parse(expiryDateStr) : null);
        if (status != null) product.setStatus(Product.ProductStatus.valueOf(status));
        product.setCountryOfOrigin(countryOfOrigin);
        product.setTags(tags);
        applyTransientSupplierFromProfile(product);

        if (removeImage) {
            product.setImage(null);
            product.setImageContentType(null);
        } else if (image != null && !image.isEmpty()) {
            product.setImage(image.getBytes());
            product.setImageContentType(image.getContentType());
        }

        return toResponse(productRepository.save(product));
    }

    // ==================== DELETE ====================
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // ==================== STOCK UPDATE ====================
    public ProductDto.Response updateStock(Long id, ProductDto.StockUpdate stockUpdate) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        int qty = stockUpdate.getQuantity();
        switch (stockUpdate.getOperation()) {
            case "ADD" -> product.setStockQuantity(product.getStockQuantity() + qty);
            case "SUBTRACT" -> {
                int newQty = product.getStockQuantity() - qty;
                if (newQty < 0) throw new IllegalArgumentException("Stock cannot go below 0");
                product.setStockQuantity(newQty);
            }
            case "SET" -> {
                if (qty < 0) throw new IllegalArgumentException("Stock cannot be negative");
                product.setStockQuantity(qty);
            }
            default -> throw new IllegalArgumentException("Invalid operation: " + stockUpdate.getOperation());
        }

        applyTransientSupplierFromProfile(product);
        return toResponse(productRepository.save(product));
    }

    // ==================== LOW STOCK ====================
    @Transactional(readOnly = true)
    public List<ProductDto.Summary> getLowStockProducts() {
        return productRepository.findLowStockProducts()
            .stream()
            .map(p -> { applyTransientSupplierFromProfile(p); return toSummary(p); })
            .collect(Collectors.toList());
    }

    // ==================== DASHBOARD STATS ====================
    @Transactional(readOnly = true)
    public ProductDto.DashboardStats getDashboardStats() {
        List<Product> all = productRepository.findAll();
        long totalProducts = all.size();
        long activeProducts = all.stream().filter(p -> p.getStatus() == Product.ProductStatus.ACTIVE).count();
        long lowStock = productRepository.countLowStockProducts();
        long outOfStock = all.stream().filter(p -> p.getStockQuantity() == 0).count();
        long totalValue = all.stream()
            .mapToLong(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStockQuantity())).longValue())
            .sum();
        long categories = all.stream().map(Product::getCategory).distinct().count();

        return ProductDto.DashboardStats.builder()
            .totalProducts(totalProducts)
            .activeProducts(activeProducts)
            .lowStockCount(lowStock)
            .outOfStockCount(outOfStock)
            .totalStockValue(totalValue)
            .categoryCount(categories)
            .build();
    }

    // ==================== MAPPERS ====================
    private void applyTransientSupplierFromProfile(Product p) {
        p.setSupplierName(null);
        p.setSupplierContact(null);
        p.setSupplierEmail(null);
        p.setSupplierAddress(null);
        if (p.getSupplierId() == null) {
            return;
        }
        supplierRepository.findByUser_Id(p.getSupplierId()).ifPresent(supplier -> {
            p.setSupplierName(supplier.getCompanyName());
            p.setSupplierContact(supplier.getContactPerson());
            p.setSupplierEmail(supplier.getEmail());
            p.setSupplierAddress(supplier.getAddress());
        });
    }

    private ProductDto.Response toResponse(Product p) {
        return ProductDto.Response.builder()
            .id(p.getId())
            .supplierId(p.getSupplierId())
            .name(p.getName())
            .brand(p.getBrand())
            .sku(p.getSku())
            .barcode(p.getBarcode())
            .category(p.getCategory())
            .description(p.getDescription())
            .ingredients(p.getIngredients())
            .usageInstructions(p.getUsageInstructions())
            .price(p.getPrice())
            .costPrice(p.getCostPrice())
            .stockQuantity(p.getStockQuantity())
            .lowStockThreshold(p.getLowStockThreshold())
            .lowStock(p.isLowStock())
            .unit(p.getUnit())
            .shade(p.getShade())
            .skinType(p.getSkinType())
            .volume(p.getVolume())
            .supplierName(p.getSupplierName())
            .supplierContact(p.getSupplierContact())
            .supplierEmail(p.getSupplierEmail())
            .supplierAddress(p.getSupplierAddress())
            .manufactureDate(p.getManufactureDate())
            .expiryDate(p.getExpiryDate())
            .imageBase64(p.getImage() != null ? Base64.getEncoder().encodeToString(p.getImage()) : null)
            .imageContentType(p.getImageContentType())
            .status(p.getStatus())
            .countryOfOrigin(p.getCountryOfOrigin())
            .tags(p.getTags())
            .createdAt(p.getCreatedAt())
            .updatedAt(p.getUpdatedAt())
            .build();
    }

    private ProductDto.Summary toSummary(Product p) {
        return ProductDto.Summary.builder()
            .id(p.getId())
            .supplierId(p.getSupplierId())
            .name(p.getName())
            .brand(p.getBrand())
            .sku(p.getSku())
            .barcode(p.getBarcode())
            .category(p.getCategory())
            .price(p.getPrice())
            .stockQuantity(p.getStockQuantity())
            .lowStockThreshold(p.getLowStockThreshold())
            .lowStock(p.isLowStock())
            .unit(p.getUnit())
            .shade(p.getShade())
            .status(p.getStatus())
            .imageBase64(p.getImage() != null ? Base64.getEncoder().encodeToString(p.getImage()) : null)
            .imageContentType(p.getImageContentType())
            .createdAt(p.getCreatedAt())
            .build();
    }
}

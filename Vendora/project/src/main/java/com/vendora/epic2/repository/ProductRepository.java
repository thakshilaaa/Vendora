package com.vendora.epic2.repository;

import com.vendora.epic2.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);
    Optional<Product> findByBarcode(String barcode);

    boolean existsBySku(String sku);
    boolean existsByBarcode(String barcode);
    boolean existsBySkuAndIdNot(String sku, Long id);
    boolean existsByBarcodeAndIdNot(String barcode, Long id);

    // Low stock products
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.lowStockThreshold AND p.status = 'ACTIVE'")
    List<Product> findLowStockProducts();

    // Count low stock
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity <= p.lowStockThreshold AND p.status = 'ACTIVE'")
    Long countLowStockProducts();

    // By category
    List<Product> findByCategory(Product.Category category);

    List<Product> findBySupplierId(Long supplierId);

    void deleteBySupplierId(Long supplierId);

    // Search by name or brand (without image for performance)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(String keyword);

    // Stats query - without image blob for performance
    @Query("SELECT p.category, COUNT(p), SUM(p.stockQuantity) FROM Product p GROUP BY p.category")
    List<Object[]> getCategoryStats();
}

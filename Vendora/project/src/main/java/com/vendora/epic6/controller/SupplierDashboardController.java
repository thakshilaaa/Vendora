package com.vendora.epic6.controller;

import com.vendora.epic2.model.Product;
import com.vendora.epic1.model.User;
import com.vendora.epic4.repository.OrderRepository;
import com.vendora.epic2.repository.ProductRepository;
import com.vendora.epic1.service.UserService;
import com.vendora.epic6.dto.ProductForm;
import com.vendora.epic6.model.Supplier;
import com.vendora.epic6.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/supplier")
@PreAuthorize("hasRole('SUPPLIER')")
public class SupplierDashboardController {

    private final SupplierService supplierService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;

    public SupplierDashboardController(SupplierService supplierService,
                                       ProductRepository productRepository,
                                       OrderRepository orderRepository,
                                       UserService userService) {
        this.supplierService = supplierService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    // -------- Dashboard --------
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = userService.getCurrentUser();
        Supplier supplier = supplierService.byUser(user);

        List<Product> products = supplier.getUser() == null
                ? List.of()
                : productRepository.findBySupplierId(supplier.getUser().getId());
        long pendingOrders = orderRepository.countPendingOrdersAll();

        model.addAttribute("supplier", supplier);
        model.addAttribute("products", products);
        model.addAttribute("productCount", products.size());
        model.addAttribute("pendingOrders", pendingOrders);
        return "epic6/html/dashboard";
    }

    /** Serves the binary image for a product owned by the current supplier. */
    @GetMapping("/product/{id}/image")
    @ResponseBody
    public ResponseEntity<byte[]> productImage(@PathVariable Long id) {
        Supplier supplier = supplierService.byUser(userService.getCurrentUser());
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (p.getSupplierId() == null || !p.getSupplierId().equals(supplier.getUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (p.getImage() == null || p.getImage().length == 0) {
            return ResponseEntity.notFound().build();
        }
        String ct = p.getImageContentType() != null ? p.getImageContentType() : "image/jpeg";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(ct))
                .body(p.getImage());
    }

    // -------- Add product --------
    @GetMapping("/product/new")
    public String newProduct(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ProductForm());
        }
        model.addAttribute("editing", false);
        model.addAttribute("categories", Product.Category.values());
        return "epic6/html/product-form";
    }

    // -------- Edit product --------
    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Supplier supplier = supplierService.byUser(userService.getCurrentUser());
        if (p.getSupplierId() == null || !p.getSupplierId().equals(supplier.getUser().getId())) {
            throw new SecurityException("Not your product");
        }

        ProductForm form = new ProductForm();
        form.setId(p.getId());
        form.setName(p.getName());
        form.setCategory(p.getCategory() != null ? p.getCategory().name() : null);
        form.setPrice(p.getPrice());
        form.setStockQuantity(p.getStockQuantity());
        form.setDescription(p.getDescription());

        model.addAttribute("form", form);
        model.addAttribute("editing", true);
        model.addAttribute("categories", Product.Category.values());
        return "epic6/html/product-form";
    }

    // -------- Save (create / update) --------
    @PostMapping("/product/save")
    public String saveProduct(@Valid @ModelAttribute("form") ProductForm form,
                              BindingResult br,
                              RedirectAttributes ra,
                              Model model) {
        if (br.hasErrors()) {
            model.addAttribute("editing", form.getId() != null);
            model.addAttribute("categories", Product.Category.values());
            return "epic6/html/product-form";
        }

        Supplier supplier = supplierService.byUser(userService.getCurrentUser());
        final Product.Category category = parseCategory(form.getCategory());

        Product p;
        if (form.getId() == null) {
            p = new Product();
            p.setSupplierId(supplier.getUser().getId());
            p.setBrand(supplier.getCompanyName() != null ? supplier.getCompanyName() : "Supplier");
            p.setLowStockThreshold(defaultLowThreshold(form.getStockQuantity()));
            p.setStatus(Product.ProductStatus.ACTIVE);
            p.setSupplierName(supplier.getCompanyName());
            p.setSupplierContact(supplier.getContactPerson());
            p.setSupplierEmail(supplier.getEmail());
            p.setSupplierAddress(supplier.getAddress());
        } else {
            p = productRepository.findById(form.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            if (p.getSupplierId() == null || !p.getSupplierId().equals(supplier.getUser().getId())) {
                throw new SecurityException("Not your product");
            }
            if (p.getBrand() == null || p.getBrand().isBlank()) {
                p.setBrand(supplier.getCompanyName() != null ? supplier.getCompanyName() : "Supplier");
            }
            if (p.getLowStockThreshold() == null) {
                p.setLowStockThreshold(defaultLowThreshold(form.getStockQuantity()));
            }
        }

        p.setName(form.getName());
        p.setCategory(category);
        p.setPrice(form.getPrice());
        p.setStockQuantity(form.getStockQuantity());
        p.setDescription(form.getDescription());

        if (form.getImage() != null && !form.getImage().isEmpty()) {
            try {
                p.setImage(form.getImage().getBytes());
                p.setImageContentType(form.getImage().getContentType());
            } catch (Exception e) {
                // ignore
            }
        }

        productRepository.save(p);

        ra.addFlashAttribute("toast",
                form.getId() == null ? "Product added" : "Product updated");
        return "redirect:/supplier/dashboard";
    }

    private static int defaultLowThreshold(Integer stock) {
        if (stock == null || stock < 1) {
            return 5;
        }
        return Math.max(1, Math.min(10, stock / 2));
    }

    private static Product.Category parseCategory(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Category is required");
        }
        String t = raw.trim();
        for (Product.Category c : Product.Category.values()) {
            if (c.name().equalsIgnoreCase(t)) {
                return c;
            }
        }
        if ("Bath & Body".equalsIgnoreCase(t) || "Bath &amp; Body".equalsIgnoreCase(t)) {
            return Product.Category.BODYCARE;
        }
        if ("MAKEUP".equalsIgnoreCase(t)) {
            return Product.Category.COSMETICS;
        }
        if ("NAILCARE".equalsIgnoreCase(t) || "SUNCARE".equalsIgnoreCase(t)
                || "MENCARE".equalsIgnoreCase(t) || "BABYCARE".equalsIgnoreCase(t)
                || "WELLNESS".equalsIgnoreCase(t)) {
            return Product.Category.BEAUTY_TOOLS;
        }
        return Product.Category.valueOf(t.toUpperCase().replace(' ', '_').replace('-', '_'));
    }

    // -------- Delete --------
    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        Supplier supplier = supplierService.byUser(userService.getCurrentUser());
        if (p.getSupplierId() == null || !p.getSupplierId().equals(supplier.getUser().getId())) {
            throw new SecurityException("Not your product");
        }
        productRepository.delete(p);
        ra.addFlashAttribute("toast", "Product removed");
        return "redirect:/supplier/dashboard";
    }
}

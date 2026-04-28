package com.vendora.epic6.controller;

import com.vendora.epic6.model.Supplier;
import com.vendora.epic6.model.SupplierStatus;
import com.vendora.epic6.service.SupplierService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/suppliers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSupplierController {

    private final SupplierService supplierService;

    public AdminSupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    /** Pending suppliers awaiting approval. */
    @GetMapping("/pending")
    public String pending(Model model) {
        model.addAttribute("suppliers", supplierService.pending());
        return "epic6/html/supplier-approvals";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes ra) {
        Supplier s = supplierService.approve(id);
        ra.addFlashAttribute("toast",
                "Approved " + s.getCompanyName() + " — credentials emailed.");
        return "redirect:/admin/suppliers/pending";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id, RedirectAttributes ra) {
        supplierService.reject(id);
        ra.addFlashAttribute("toast", "Supplier rejected.");
        return "redirect:/admin/suppliers/pending";
    }

    /** All suppliers, with search + status filter. */
    @GetMapping
    public String all(@RequestParam(required = false) String q,
                      @RequestParam(required = false) SupplierStatus status,
                      Model model) {
        List<Supplier> suppliers = supplierService.search(q);
        if (status != null) {
            suppliers = suppliers.stream().filter(s -> s.getStatus() == status).toList();
        }
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("q", q);
        model.addAttribute("statuses", SupplierStatus.values());
        model.addAttribute("selectedStatus", status);
        return "epic6/html/supplier-list";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        supplierService.delete(id);
        ra.addFlashAttribute("toast", "Supplier removed.");
        return "redirect:/admin/suppliers";
    }
}

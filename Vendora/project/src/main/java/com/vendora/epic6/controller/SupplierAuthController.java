package com.vendora.epic6.controller;

import com.vendora.epic6.dto.SupplierRegistrationDto;
import com.vendora.epic6.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Public registration endpoints — no authentication required.
 */
@Controller
@RequestMapping("/supplier")
public class SupplierAuthController {

    private final SupplierService supplierService;

    public SupplierAuthController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new SupplierRegistrationDto());
        }
        return "epic1/registration/supplier-register";
    }

    @PostMapping("/register")
    public String submit(@Valid @ModelAttribute("form") SupplierRegistrationDto form,
                         BindingResult br,
                         RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "epic1/registration/supplier-register";
        }
        try {
            supplierService.register(form);
        } catch (IllegalStateException ex) {
            br.rejectValue("email", "duplicate", ex.getMessage());
            return "epic1/registration/supplier-register";
        }
        ra.addFlashAttribute("success",
                "Application received! We'll email you once an admin reviews it.");
        return "redirect:/supplier/register";
    }
}

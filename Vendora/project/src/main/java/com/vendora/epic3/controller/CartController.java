package com.vendora.epic3.controller;

import com.vendora.epic3.dto.CartItemDto;
import com.vendora.epic3.dto.CheckoutRequest;
import com.vendora.epic3.model.LocationType;
import com.vendora.epic3.service.CartService;
import com.vendora.epic3.service.DeliveryChargeService;
import com.vendora.epic4.model.Order;
import com.vendora.epic1.exception.ForbiddenException;
import com.vendora.epic1.exception.UnauthorizedException;
import com.vendora.epic1.model.User;
import com.vendora.epic4.service.OrderService;
import com.vendora.epic1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handles the cart page, AJAX cart updates, and the checkout flow.
 */
@Controller
public class CartController {

    private final CartService cartService;
    private final DeliveryChargeService deliveryChargeService;
    private final UserService userService;
    private final OrderService orderService;

    public CartController(CartService cartService,
                          DeliveryChargeService deliveryChargeService,
                          UserService userService,
                          OrderService orderService) {
        this.cartService = cartService;
        this.deliveryChargeService = deliveryChargeService;
        this.userService = userService;
        this.orderService = orderService;
    }

    // ---------------------------- Cart page ----------------------------

    @GetMapping("/cart")
    public String viewCart(Model model) {
        User user = userService.getCurrentUser();
        List<CartItemDto> items = cartService.listItems(user);
        BigDecimal subtotal = items.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("items", items);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("defaultDeliveryFee", new BigDecimal("500"));
        return "epic3/html/cart";
    }

    /** REST endpoint used by cart.js to refresh the grid after AJAX changes. */
    @GetMapping("/cart/api/items")
    @ResponseBody
    public List<CartItemDto> apiItems() {
        User user = userService.getCurrentUser();
        return cartService.listItems(user);
    }

    /** AJAX +/- quantity update. Returns the updated item DTO. */
    @PostMapping("/cart/update")
    @ResponseBody
    public CartItemDto updateQuantity(@RequestParam Long itemId,
                                      @RequestParam int quantity) {
        User user = userService.getCurrentUser();
        return cartService.updateQuantity(user, itemId, quantity);
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes ra) {
        User user = userService.getCurrentUser();
        cartService.addToCart(user, productId, quantity);
        ra.addFlashAttribute("toast", "Added to your cart");
        return "redirect:/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeOne(@PathVariable Long id, RedirectAttributes ra) {
        User user = userService.getCurrentUser();
        cartService.removeItem(user, id);
        ra.addFlashAttribute("toast", "Item removed from your cart");
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove-selected")
    public String removeSelected(@RequestParam(name = "itemIds", required = false) List<Long> itemIds,
                                 RedirectAttributes ra) {
        User user = userService.getCurrentUser();
        cartService.removeItems(user, itemIds);
        ra.addFlashAttribute("toast", "Selected items removed");
        return "redirect:/cart";
    }

    // --------------------------- Checkout ---------------------------

    @GetMapping("/checkout")
    public String checkoutPage(@RequestParam(name = "itemIds", required = false) List<Long> itemIds,
                               Model model) {
        User user = userService.getCurrentUser();
        List<CartItemDto> all = cartService.listItems(user);
        List<CartItemDto> selected = all.stream()
                .filter(i -> itemIds == null || itemIds.contains(i.getId()))
                .toList();

        BigDecimal subtotal = selected.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CheckoutRequest form = new CheckoutRequest();
        form.setItemIds(selected.stream().map(CartItemDto::getId).toList());
        form.setLocationType(LocationType.SUBURB);
        form.setPaymentMethod("COD");

        model.addAttribute("items", selected);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("defaultFee", deliveryChargeService.calculate(LocationType.SUBURB));
        model.addAttribute("locations", LocationType.values());
        model.addAttribute("checkoutRequest", form);
        return "epic3/html/checkout";
    }

    @PostMapping("/checkout")
    public String submitCheckout(@Valid @ModelAttribute("checkoutRequest") CheckoutRequest req,
                                 BindingResult br,
                                 Model model,
                                 RedirectAttributes ra) {
        User user = userService.getCurrentUser();
        if (br.hasErrors()) {
            // Re-populate the page with the user's selection
            List<CartItemDto> all = cartService.listItems(user);
            List<CartItemDto> selected = all.stream()
                    .filter(i -> req.getItemIds() != null && req.getItemIds().contains(i.getId()))
                    .toList();
            BigDecimal subtotal = selected.stream()
                    .map(CartItemDto::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("items", selected);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("locations", LocationType.values());
            model.addAttribute("defaultFee", deliveryChargeService.calculate(
                    req.getLocationType() != null ? req.getLocationType() : LocationType.SUBURB));
            model.addAttribute("checkoutRequest", req);
            return "epic3/html/checkout";
        }

        try {
            Order order = orderService.checkout(req, user.getId());
            ra.addFlashAttribute("toast",
                    "Order #" + order.getId() + " placed successfully!");
            return "redirect:/orders/" + order.getId();
        } catch (ForbiddenException e) {
            ra.addFlashAttribute("toast", e.getMessage());
            return "redirect:/cart";
        }
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String cartUnauthorized() {
        return "redirect:/login";
    }
}

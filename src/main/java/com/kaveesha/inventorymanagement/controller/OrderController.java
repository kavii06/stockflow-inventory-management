package com.kaveesha.inventorymanagement.controller;

import com.kaveesha.inventorymanagement.dto.OrderRequest;
import com.kaveesha.inventorymanagement.entity.InventoryOrder;
import com.kaveesha.inventorymanagement.entity.Product;
import com.kaveesha.inventorymanagement.exception.InsufficientStockException;
import com.kaveesha.inventorymanagement.service.OrderService;
import com.kaveesha.inventorymanagement.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final ProductService productService;
    private final OrderService orderService;

    public OrderController(ProductService productService,
                           OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/new")
    public String showOrderForm(
            @RequestParam Long productId,
            Model model) {

        Product product = productService.findById(productId);

        model.addAttribute("product", product);
        model.addAttribute(
                "orderRequest",
                new OrderRequest(productId, 1)
        );

        return "orders/form";
    }

    @PostMapping("/confirm")
    public String confirmOrder(
            @Valid @ModelAttribute("orderRequest")
            OrderRequest orderRequest,
            BindingResult result,
            Model model) {

        Product product = productService.findById(
                orderRequest.getProductId()
        );

        if (orderRequest.getQuantity() != null
                && orderRequest.getQuantity()
                > product.getStockQuantity()) {

            result.rejectValue(
                    "quantity",
                    "insufficient",
                    "Only " + product.getStockQuantity()
                            + " units are available"
            );
        }

        if (result.hasErrors()) {
            model.addAttribute("product", product);
            return "orders/form";
        }

        model.addAttribute("product", product);
        model.addAttribute("orderRequest", orderRequest);
        model.addAttribute(
                "totalPrice",
                product.getPrice().multiply(
                        java.math.BigDecimal.valueOf(
                                orderRequest.getQuantity()
                        )
                )
        );

        return "orders/confirm";
    }

    @PostMapping("/place")
    public String placeOrder(
            @ModelAttribute OrderRequest orderRequest,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            InventoryOrder order = orderService.placeOrder(
                    orderRequest.getProductId(),
                    orderRequest.getQuantity(),
                    authentication.getName()
            );

            return "redirect:/orders/success/" + order.getId();

        } catch (InsufficientStockException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return "redirect:/orders/new?productId="
                    + orderRequest.getProductId();
        }
    }

    @GetMapping("/success/{id}")
    public String showSuccess(
            @PathVariable Long id,
            Model model) {

        model.addAttribute("order", orderService.findById(id));
        return "orders/success";
    }

    @GetMapping
    public String showMyOrders(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "orders",
                orderService.findOrdersForUser(
                        authentication.getName()
                )
        );

        return "orders/list";
    }
}
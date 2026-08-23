package com.kaveesha.inventorymanagement.controller;

import com.kaveesha.inventorymanagement.entity.Product;
import com.kaveesha.inventorymanagement.service.CategoryService;
import com.kaveesha.inventorymanagement.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public HomeController(CategoryService categoryService,
                          ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    public String homePage(Authentication authentication, Model model) {
        List<Product> products = productService.findAll();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        long lowStockCount = products.stream()
                .filter(product -> product.getStockQuantity() <= 5)
                .count();

        int totalStock = products.stream()
                .mapToInt(Product::getStockQuantity)
                .sum();

        model.addAttribute("username", authentication.getName());
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("categoryCount", categoryService.findAll().size());
        model.addAttribute("productCount", products.size());
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("totalStock", totalStock);

        return "home";
    }
}
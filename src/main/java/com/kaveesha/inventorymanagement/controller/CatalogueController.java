package com.kaveesha.inventorymanagement.controller;

import com.kaveesha.inventorymanagement.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CatalogueController {

    private final ProductService productService;

    public CatalogueController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/catalog")
    public String showCatalogue(
            @RequestParam(required = false) String keyword,
            Model model) {

        model.addAttribute("products", productService.search(keyword));
        model.addAttribute("keyword", keyword);

        return "catalog";
    }
}
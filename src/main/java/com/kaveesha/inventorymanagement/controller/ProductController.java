package com.kaveesha.inventorymanagement.controller;

import com.kaveesha.inventorymanagement.entity.Category;
import com.kaveesha.inventorymanagement.entity.Product;
import com.kaveesha.inventorymanagement.exception.DuplicateResourceException;
import com.kaveesha.inventorymanagement.service.CategoryService;
import com.kaveesha.inventorymanagement.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService,
                             CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String keyword,
            Model model) {

        model.addAttribute("products", productService.search(keyword));
        model.addAttribute("keyword", keyword);

        return "products/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Product product = new Product();
        product.setCategory(new Category());

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "Add Product");

        return "products/form";
    }

    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (product.getCategory() == null
                || product.getCategory().getId() == null) {
            result.rejectValue(
                    "category",
                    "required",
                    "Category is required"
            );
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute(
                    "pageTitle",
                    product.getId() == null
                            ? "Add Product"
                            : "Edit Product"
            );

            return "products/form";
        }

        try {
            productService.save(product);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Product saved successfully"
            );

            return "redirect:/admin/products";

        } catch (DuplicateResourceException exception) {
            result.rejectValue(
                    "sku",
                    "duplicate",
                    exception.getMessage()
            );

            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute(
                    "pageTitle",
                    product.getId() == null
                            ? "Add Product"
                            : "Edit Product"
            );

            return "products/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "Edit Product");

        return "products/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        productService.delete(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Product deleted successfully"
        );

        return "redirect:/admin/products";
    }
}
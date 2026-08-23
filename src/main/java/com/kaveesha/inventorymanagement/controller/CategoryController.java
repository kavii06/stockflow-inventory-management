package com.kaveesha.inventorymanagement.controller;

import com.kaveesha.inventorymanagement.entity.Category;
import com.kaveesha.inventorymanagement.exception.DuplicateResourceException;
import com.kaveesha.inventorymanagement.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Add Category");
        return "categories/form";
    }

    @PostMapping("/save")
    public String saveCategory(
            @Valid @ModelAttribute("category") Category category,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle",
                    category.getId() == null ? "Add Category" : "Edit Category");
            return "categories/form";
        }

        try {
            categoryService.save(category);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Category saved successfully");
            return "redirect:/admin/categories";
        } catch (DuplicateResourceException exception) {
            result.rejectValue("name", "duplicate", exception.getMessage());
            model.addAttribute("pageTitle",
                    category.getId() == null ? "Add Category" : "Edit Category");
            return "categories/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        model.addAttribute("pageTitle", "Edit Category");
        return "categories/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Category deleted successfully");
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", exception.getMessage());
        }

        return "redirect:/admin/categories";
    }
}
package com.kaveesha.inventorymanagement.service;

import com.kaveesha.inventorymanagement.entity.Category;
import com.kaveesha.inventorymanagement.exception.DuplicateResourceException;
import com.kaveesha.inventorymanagement.exception.ResourceNotFoundException;
import com.kaveesha.inventorymanagement.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with ID: " + id));
    }

    @Transactional
    public Category save(Category category) {
        categoryRepository.findByNameIgnoreCase(category.getName())
                .filter(existing -> category.getId() == null
                        || !existing.getId().equals(category.getId()))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "A category with this name already exists");
                });

        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = findById(id);

        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete a category that contains products");
        }

        categoryRepository.delete(category);
    }
}
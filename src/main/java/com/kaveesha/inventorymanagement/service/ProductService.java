package com.kaveesha.inventorymanagement.service;

import com.kaveesha.inventorymanagement.entity.Product;
import com.kaveesha.inventorymanagement.exception.DuplicateResourceException;
import com.kaveesha.inventorymanagement.exception.ResourceNotFoundException;
import com.kaveesha.inventorymanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository,
                          CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with ID: " + id));
    }

    public List<Product> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional
    public Product save(Product product) {
        productRepository.findBySkuIgnoreCase(product.getSku())
                .filter(existing -> product.getId() == null
                        || !existing.getId().equals(product.getId()))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "A product with this SKU already exists");
                });

        product.setCategory(
                categoryService.findById(product.getCategory().getId())
        );

        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
}
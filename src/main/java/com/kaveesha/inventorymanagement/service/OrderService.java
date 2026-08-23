package com.kaveesha.inventorymanagement.service;

import com.kaveesha.inventorymanagement.entity.InventoryOrder;
import com.kaveesha.inventorymanagement.entity.OrderStatus;
import com.kaveesha.inventorymanagement.entity.Product;
import com.kaveesha.inventorymanagement.exception.InsufficientStockException;
import com.kaveesha.inventorymanagement.exception.ResourceNotFoundException;
import com.kaveesha.inventorymanagement.repository.OrderRepository;
import com.kaveesha.inventorymanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public InventoryOrder placeOrder(
            Long productId,
            Integer quantity,
            String username) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with ID: " + productId
                        )
                );

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Only " + product.getStockQuantity()
                            + " units are currently available"
            );
        }

        product.setStockQuantity(
                product.getStockQuantity() - quantity
        );

        productRepository.save(product);

        InventoryOrder order = new InventoryOrder();
        order.setOrderNumber(createOrderNumber());
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setOrderedBy(username);
        order.setStatus(OrderStatus.CONFIRMED);

        BigDecimal total = product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));

        order.setTotalPrice(total);

        return orderRepository.save(order);
    }

    public List<InventoryOrder> findOrdersForUser(String username) {
        return orderRepository
                .findByOrderedByOrderByCreatedAtDesc(username);
    }

    public List<InventoryOrder> findAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public InventoryOrder findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with ID: " + id
                        )
                );
    }

    private String createOrderNumber() {
        return "ORD-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}
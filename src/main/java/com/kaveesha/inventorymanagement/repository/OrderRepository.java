package com.kaveesha.inventorymanagement.repository;

import com.kaveesha.inventorymanagement.entity.InventoryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository
        extends JpaRepository<InventoryOrder, Long> {

    List<InventoryOrder> findByOrderedByOrderByCreatedAtDesc(
            String username
    );

    List<InventoryOrder> findAllByOrderByCreatedAtDesc();
}
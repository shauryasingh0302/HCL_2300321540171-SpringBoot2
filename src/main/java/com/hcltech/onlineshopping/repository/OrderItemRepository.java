package com.hcltech.onlineshopping.repository;

import com.hcltech.onlineshopping.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}

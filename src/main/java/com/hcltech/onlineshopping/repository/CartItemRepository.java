package com.hcltech.onlineshopping.repository;

import com.hcltech.onlineshopping.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}

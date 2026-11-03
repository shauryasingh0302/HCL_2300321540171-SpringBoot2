package com.hcltech.onlineshopping.repository;

import com.hcltech.onlineshopping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByProductNameContainingIgnoreCase(String name);
}

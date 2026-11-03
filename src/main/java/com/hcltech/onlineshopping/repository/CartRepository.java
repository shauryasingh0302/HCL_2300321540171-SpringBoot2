package com.hcltech.onlineshopping.repository;

import com.hcltech.onlineshopping.entity.Cart;
import com.hcltech.onlineshopping.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomer(Customer customer);
}

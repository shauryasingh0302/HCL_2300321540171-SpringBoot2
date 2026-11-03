package com.hcltech.onlineshopping.repository;

import com.hcltech.onlineshopping.entity.CustomerOrder;
import com.hcltech.onlineshopping.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByCustomer(Customer customer);
}

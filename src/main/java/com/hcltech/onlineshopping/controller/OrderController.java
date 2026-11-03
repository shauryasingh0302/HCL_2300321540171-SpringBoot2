package com.hcltech.onlineshopping.controller;

import com.hcltech.onlineshopping.entity.CustomerOrder;
import com.hcltech.onlineshopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place/{customerId}")
    public ResponseEntity<CustomerOrder> placeOrder(@PathVariable Long customerId) {
        CustomerOrder order = orderService.placeOrder(customerId);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<CustomerOrder> getOrderById(@PathVariable Long orderId) {
        CustomerOrder order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping(value = {"/customer/{customerId}", "/history/{customerId}"})
    public ResponseEntity<List<CustomerOrder>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<CustomerOrder> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping
    public ResponseEntity<List<CustomerOrder>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<CustomerOrder> cancelOrder(@PathVariable Long orderId) {
        CustomerOrder order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(order);
    }
}

package com.hcltech.onlineshopping.service;

import com.hcltech.onlineshopping.entity.*;
import com.hcltech.onlineshopping.exception.BadRequestException;
import com.hcltech.onlineshopping.exception.InsufficientStockException;
import com.hcltech.onlineshopping.exception.ResourceNotFoundException;
import com.hcltech.onlineshopping.repository.CustomerOrderRepository;
import com.hcltech.onlineshopping.repository.OrderItemRepository;
import com.hcltech.onlineshopping.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public CustomerOrder placeOrder(Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        Cart cart = cartService.getCartByCustomerId(customerId);

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cannot place order with an empty cart");
        }

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("PLACED");

        double totalAmount = 0;

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            // Verify stock availability
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getProductName()
                        + ". Available: " + product.getQuantity() 
                        + ", Requested: " + cartItem.getQuantity());
            }

            // Deduct stock
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setCustomerOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            order.getOrderItems().add(orderItem);

            totalAmount += cartItem.getPrice();
        }

        order.setTotalAmount(totalAmount);
        CustomerOrder savedOrder = customerOrderRepository.save(order);

        // Clear the cart after placing order
        cartService.clearCart(customerId);

        return savedOrder;
    }

    public CustomerOrder getOrderById(Long orderId) {
        return customerOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    public List<CustomerOrder> getOrdersByCustomerId(Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return customerOrderRepository.findByCustomer(customer);
    }

    public List<CustomerOrder> getAllOrders() {
        return customerOrderRepository.findAll();
    }

    @Transactional
    public CustomerOrder cancelOrder(Long orderId) {
        CustomerOrder order = getOrderById(orderId);

        if (!"PLACED".equals(order.getOrderStatus())) {
            throw new BadRequestException("Only orders with status 'PLACED' can be cancelled. Current status: " + order.getOrderStatus());
        }

        // Restore product stock
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.setQuantity(product.getQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }

        order.setOrderStatus("CANCELLED");
        return customerOrderRepository.save(order);
    }
}

package com.hcltech.onlineshopping.controller;

import com.hcltech.onlineshopping.entity.*;
import com.hcltech.onlineshopping.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private Customer testCustomer;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        customerOrderRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();

        testCustomer = customerRepository.save(
                new Customer("Bob Builder", "bob.order@test.com", "pass123", "9876543210", "Hyderabad")
        );
        testProduct = productRepository.save(
                new Product("Gaming Monitor", "4K 144Hz", 25000.0, 5, "Electronics")
        );
    }

    @Test
    void testPlaceOrder_Success() throws Exception {
        // Add 2 items to cart
        mockMvc.perform(post("/api/cart/add/" + testCustomer.getCustomerId() + "/" + testProduct.getProductId() + "?quantity=2"))
                .andExpect(status().isCreated());

        // Place order
        mockMvc.perform(post("/api/orders/place/" + testCustomer.getCustomerId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.orderStatus", is("PLACED")))
                .andExpect(jsonPath("$.totalAmount", is(50000.0)))
                .andExpect(jsonPath("$.orderItems", hasSize(1)));

        // Verify stock deducted from 5 to 3
        Product updatedProduct = productRepository.findById(testProduct.getProductId()).orElseThrow();
        assertEquals(3, updatedProduct.getQuantity());

        // Verify cart is cleared
        mockMvc.perform(get("/api/cart/" + testCustomer.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems", hasSize(0)));
    }

    @Test
    void testPlaceOrder_EmptyCart_Fails() throws Exception {
        // Attempt to place order with no items in cart
        mockMvc.perform(post("/api/orders/place/" + testCustomer.getCustomerId()))
                .andExpect(status().isNotFound()); // Cart not found or empty
    }

    @Test
    void testGetOrderById() throws Exception {
        mockMvc.perform(post("/api/cart/add/" + testCustomer.getCustomerId() + "/" + testProduct.getProductId() + "?quantity=1"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/orders/place/" + testCustomer.getCustomerId()))
                .andExpect(status().isCreated());

        CustomerOrder order = customerOrderRepository.findByCustomer(testCustomer).get(0);

        mockMvc.perform(get("/api/orders/" + order.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus", is("PLACED")))
                .andExpect(jsonPath("$.totalAmount", is(25000.0)));
    }

    @Test
    void testGetOrdersByCustomer() throws Exception {
        mockMvc.perform(post("/api/cart/add/" + testCustomer.getCustomerId() + "/" + testProduct.getProductId() + "?quantity=1"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/orders/place/" + testCustomer.getCustomerId()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/orders/customer/" + testCustomer.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/orders/history/" + testCustomer.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testCancelOrder_SuccessAndRestoresStock() throws Exception {
        mockMvc.perform(post("/api/cart/add/" + testCustomer.getCustomerId() + "/" + testProduct.getProductId() + "?quantity=2"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/orders/place/" + testCustomer.getCustomerId()))
                .andExpect(status().isCreated());

        // Stock was 5, 2 ordered -> now 3
        Product productAfterOrder = productRepository.findById(testProduct.getProductId()).orElseThrow();
        assertEquals(3, productAfterOrder.getQuantity());

        CustomerOrder order = customerOrderRepository.findByCustomer(testCustomer).get(0);

        // Cancel order
        mockMvc.perform(put("/api/orders/cancel/" + order.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus", is("CANCELLED")));

        // Stock restored back to 5
        Product productAfterCancel = productRepository.findById(testProduct.getProductId()).orElseThrow();
        assertEquals(5, productAfterCancel.getQuantity());

        // Trying to cancel again should fail
        mockMvc.perform(put("/api/orders/cancel/" + order.getOrderId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Only orders with status 'PLACED' can be cancelled")));
    }
}

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {

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
                new Customer("Alice Smith", "alice.cart@test.com", "pass123", "9876543210", "Bangalore")
        );
        testProduct = productRepository.save(
                new Product("Mechanical Keyboard", "RGB Gaming", 3500.0, 10, "Accessories")
        );
    }

    @Test
    void testAddProductToCart_Success() throws Exception {
        mockMvc.perform(post("/api/cart/add/" + testCustomer.getCustomerId() + "/" + testProduct.getProductId() + "?quantity=2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.cartItems[0].quantity", is(2)))
                .andExpect(jsonPath("$.cartItems[0].price", is(7000.0)))
                .andExpect(jsonPath("$.cartTotal", is(7000.0)));
    }

    @Test
    void testAddProductToCart_UpdateQuantityWhenAddedAgain() throws Exception {
        // Add 2 items first
        mockMvc.perform(post("/api/cart/add/" + testCustomer.getCustomerId() + "/" + testProduct.getProductId() + "?quantity=2"))
                .andExpect(status().isCreated());

        // Add 3 more items of same product
        mockMvc.perform(post("/api/cart/add/" + testCustomer.getCustomerId() + "/" + testProduct.getProductId() + "?quantity=3"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.cartItems[0].quantity", is(5)))
                .andExpect(jsonPath("$.cartItems[0].price", is(17500.0)))
                .andExpect(jsonPath("$.cartTotal", is(17500.0)));
    }

    @Test
    void testAddProductToCart_ExceedStock_Fails() throws Exception {
        // Product quantity is 10, request 15
        mockMvc.perform(post("/api/cart/add/" + testCustomer.getCustomerId() + "/" + testProduct.getProductId() + "?quantity=15"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Insufficient stock")));
    }

    @Test
    void testGetCartByCustomerId() throws Exception {
        mockMvc.perform(post("/api/cart/add/" + testCustomer.getCustomerId() + "/" + testProduct.getProductId() + "?quantity=1"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/cart/" + testCustomer.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.cartTotal", is(3500.0)));
    }

    @Test
    void testUpdateCartItemQuantity_Success() throws Exception {
        mockMvc.perform(post("/api/cart/add/" + testCustomer.getCustomerId() + "/" + testProduct.getProductId() + "?quantity=1"))
                .andExpect(status().isCreated());

        Long cartItemId = cartItemRepository.findAll().get(0).getCartItemId();

        mockMvc.perform(put("/api/cart/update/" + cartItemId + "?quantity=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(4)))
                .andExpect(jsonPath("$.price", is(14000.0)));
    }

    @Test
    void testRemoveCartItem_Success() throws Exception {
        mockMvc.perform(post("/api/cart/add/" + testCustomer.getCustomerId() + "/" + testProduct.getProductId() + "?quantity=1"))
                .andExpect(status().isCreated());

        Long cartItemId = cartItemRepository.findAll().get(0).getCartItemId();

        mockMvc.perform(delete("/api/cart/remove/" + cartItemId))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart item removed successfully"));
    }

    @Test
    void testClearCart_Success() throws Exception {
        mockMvc.perform(post("/api/cart/add/" + testCustomer.getCustomerId() + "/" + testProduct.getProductId() + "?quantity=1"))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/cart/clear/" + testCustomer.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart cleared successfully"));

        mockMvc.perform(get("/api/cart/" + testCustomer.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems", hasSize(0)))
                .andExpect(jsonPath("$.cartTotal", is(0.0)));
    }
}

package com.hcltech.onlineshopping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.onlineshopping.entity.Product;
import com.hcltech.onlineshopping.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        customerOrderRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void testAddProduct_Success() throws Exception {
        Product product = new Product("MacBook Pro", "Apple M3 Chip", 149999.0, 20, "Electronics");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").exists())
                .andExpect(jsonPath("$.productName", is("MacBook Pro")))
                .andExpect(jsonPath("$.price", is(149999.0)))
                .andExpect(jsonPath("$.quantity", is(20)));
    }

    @Test
    void testAddProduct_InvalidPrice_Fails() throws Exception {
        Product product = new Product("MacBook Pro", "Apple M3 Chip", 0.0, 20, "Electronics");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    void testAddProduct_NegativeQuantity_Fails() throws Exception {
        Product product = new Product("MacBook Pro", "Apple M3 Chip", 149999.0, -5, "Electronics");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.quantity").exists());
    }

    @Test
    void testAddProduct_BlankName_Fails() throws Exception {
        Product product = new Product("", "Apple M3 Chip", 149999.0, 10, "Electronics");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.productName").exists());
    }

    @Test
    void testGetAllProducts() throws Exception {
        productRepository.save(new Product("Item A", "Desc A", 100.0, 10, "Cat 1"));
        productRepository.save(new Product("Item B", "Desc B", 200.0, 15, "Cat 2"));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetProductById_Success() throws Exception {
        Product saved = productRepository.save(new Product("Sony Headphones", "ANC", 24999.0, 10, "Audio"));

        mockMvc.perform(get("/api/products/" + saved.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName", is("Sony Headphones")));
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        mockMvc.perform(get("/api/products/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        Product saved = productRepository.save(new Product("Old Mouse", "Wired", 500.0, 5, "Accessories"));

        Product updated = new Product("New Mouse", "Wireless", 1200.0, 15, "Accessories");

        mockMvc.perform(put("/api/products/" + saved.getProductId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName", is("New Mouse")))
                .andExpect(jsonPath("$.price", is(1200.0)));
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        Product saved = productRepository.save(new Product("To Delete", "Desc", 10.0, 1, "Misc"));

        mockMvc.perform(delete("/api/products/" + saved.getProductId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        productRepository.save(new Product("Galaxy S24", "Phone", 79999.0, 10, "Mobile"));
        productRepository.save(new Product("Pixel 8", "Phone", 69999.0, 12, "Mobile"));
        productRepository.save(new Product("iPad Air", "Tablet", 59999.0, 5, "Tablet"));

        mockMvc.perform(get("/api/products/category/Mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testSearchProductsByName() throws Exception {
        productRepository.save(new Product("Dell XPS 15", "Laptop", 120000.0, 5, "Computers"));
        productRepository.save(new Product("Dell Inspiron", "Laptop", 60000.0, 8, "Computers"));
        productRepository.save(new Product("HP Pavilion", "Laptop", 55000.0, 7, "Computers"));

        mockMvc.perform(get("/api/products/search?name=dell"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/api/products?name=dell"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}

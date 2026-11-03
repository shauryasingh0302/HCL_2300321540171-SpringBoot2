package com.hcltech.onlineshopping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.onlineshopping.entity.Customer;
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
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        customerOrderRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void testRegisterCustomer_Success() throws Exception {
        Customer customer = new Customer("John Doe", "john@example.com", "pass123", "9876543210", "Mumbai");

        mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").exists())
                .andExpect(jsonPath("$.customerName", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.mobile", is("9876543210")));
    }

    @Test
    void testRegisterCustomer_BlankName_Fails() throws Exception {
        Customer customer = new Customer("", "valid@example.com", "pass123", "9876543210", "Mumbai");

        mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.customerName").exists());
    }

    @Test
    void testRegisterCustomer_InvalidMobile_Fails() throws Exception {
        Customer customer = new Customer("John Doe", "valid2@example.com", "pass123", "12345", "Mumbai");

        mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.mobile").exists());
    }

    @Test
    void testRegisterCustomer_DuplicateEmail_Fails() throws Exception {
        Customer customer1 = new Customer("John Doe", "duplicate@example.com", "pass123", "9876543210", "Mumbai");
        customerRepository.save(customer1);

        Customer customer2 = new Customer("Jane Doe", "duplicate@example.com", "pass456", "9876543211", "Delhi");

        mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void testGetAllCustomers() throws Exception {
        customerRepository.save(new Customer("Customer 1", "c1@test.com", "pass", "1234567890", "City 1"));
        customerRepository.save(new Customer("Customer 2", "c2@test.com", "pass", "1234567891", "City 2"));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetCustomerById_Success() throws Exception {
        Customer saved = customerRepository.save(new Customer("Alice", "alice@test.com", "pass", "9998887776", "Pune"));

        mockMvc.perform(get("/api/customers/" + saved.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("alice@test.com")));
    }

    @Test
    void testGetCustomerById_NotFound() throws Exception {
        mockMvc.perform(get("/api/customers/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    void testUpdateCustomer_Success() throws Exception {
        Customer saved = customerRepository.save(new Customer("Bob", "bob@test.com", "pass", "8887776665", "Goa"));

        Customer updatedDetails = new Customer("Bob Updated", "bob_updated@test.com", "newpass", "8887776665", "Goa");

        mockMvc.perform(put("/api/customers/" + saved.getCustomerId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName", is("Bob Updated")))
                .andExpect(jsonPath("$.email", is("bob_updated@test.com")));
    }

    @Test
    void testDeleteCustomer_Success() throws Exception {
        Customer saved = customerRepository.save(new Customer("Charlie", "charlie@test.com", "pass", "7776665554", "Chennai"));

        mockMvc.perform(delete("/api/customers/" + saved.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer deleted successfully"));
    }
}

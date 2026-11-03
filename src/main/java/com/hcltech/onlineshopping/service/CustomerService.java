package com.hcltech.onlineshopping.service;

import com.hcltech.onlineshopping.entity.Customer;
import com.hcltech.onlineshopping.exception.DuplicateResourceException;
import com.hcltech.onlineshopping.exception.ResourceNotFoundException;
import com.hcltech.onlineshopping.repository.CartRepository;
import com.hcltech.onlineshopping.repository.CustomerOrderRepository;
import com.hcltech.onlineshopping.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    public Customer registerCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new DuplicateResourceException("Customer with email " + customer.getEmail() + " already exists");
        }
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = getCustomerById(id);
        
        // Check if email is being changed to an already existing email
        if (!customer.getEmail().equals(customerDetails.getEmail()) 
                && customerRepository.existsByEmail(customerDetails.getEmail())) {
            throw new DuplicateResourceException("Customer with email " + customerDetails.getEmail() + " already exists");
        }
        
        customer.setCustomerName(customerDetails.getCustomerName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPassword(customerDetails.getPassword());
        customer.setMobile(customerDetails.getMobile());
        customer.setCity(customerDetails.getCity());
        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        cartRepository.findByCustomer(customer).ifPresent(cartRepository::delete);
        customerOrderRepository.findByCustomer(customer).forEach(customerOrderRepository::delete);
        customerRepository.delete(customer);
    }
}

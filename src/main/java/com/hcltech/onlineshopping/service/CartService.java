package com.hcltech.onlineshopping.service;

import com.hcltech.onlineshopping.entity.*;
import com.hcltech.onlineshopping.exception.BadRequestException;
import com.hcltech.onlineshopping.exception.InsufficientStockException;
import com.hcltech.onlineshopping.exception.ResourceNotFoundException;
import com.hcltech.onlineshopping.repository.CartItemRepository;
import com.hcltech.onlineshopping.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    @Transactional
    public Cart addProductToCart(Long customerId, Long productId, int quantity) {
        Customer customer = customerService.getCustomerById(customerId);
        Product product = productService.getProductById(productId);

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getProductName() 
                    + ". Available: " + product.getQuantity() + ", Requested: " + quantity);
        }

        // Get or create cart for customer
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    newCart.setCreatedDate(LocalDate.now());
                    return cartRepository.save(newCart);
                });

        // Check if product already exists in cart
        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            if (product.getQuantity() < newQuantity) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getProductName() 
                        + ". Available: " + product.getQuantity() + ", Requested total: " + newQuantity);
            }
            cartItem.setQuantity(newQuantity);
            cartItem.setPrice(product.getPrice() * newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice() * quantity);
            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        return cartRepository.findByCustomer(customer).orElse(cart);
    }

    public Cart getCartByCustomerId(Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer id: " + customerId));
    }

    @Transactional
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        Product product = cartItem.getProduct();
        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getProductName() 
                    + ". Available: " + product.getQuantity() + ", Requested: " + quantity);
        }

        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice() * quantity);
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer id: " + customerId));
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
}

package com.hcltech.onlineshopping.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToOne
    @JoinColumn(name = "customer_id", unique = true)
    private Customer customer;

    private LocalDate createdDate;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<CartItem> cartItems = new ArrayList<>();

    public Cart() {
    }

    public Cart(Customer customer, LocalDate createdDate) {
        this.customer = customer;
        this.createdDate = createdDate;
    }

    public Cart(Long cartId, Customer customer, LocalDate createdDate, List<CartItem> cartItems) {
        this.cartId = cartId;
        this.customer = customer;
        this.createdDate = createdDate;
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public List<CartItem> getCartItems() {
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @JsonProperty("cartTotal")
    public Double getCartTotal() {
        if (cartItems == null || cartItems.isEmpty()) {
            return 0.0;
        }
        return cartItems.stream()
                .mapToDouble(item -> item.getPrice() != null ? item.getPrice() : 0.0)
                .sum();
    }
}

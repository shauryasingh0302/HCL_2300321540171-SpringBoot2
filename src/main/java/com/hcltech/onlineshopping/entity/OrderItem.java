package com.hcltech.onlineshopping.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "customer_order_id")
    @JsonIgnore
    private CustomerOrder customerOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    private Double price;

    public OrderItem() {
    }

    public OrderItem(CustomerOrder customerOrder, Product product, Integer quantity, Double price) {
        this.customerOrder = customerOrder;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItem(Long orderItemId, CustomerOrder customerOrder, Product product, Integer quantity, Double price) {
        this.orderItemId = orderItemId;
        this.customerOrder = customerOrder;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

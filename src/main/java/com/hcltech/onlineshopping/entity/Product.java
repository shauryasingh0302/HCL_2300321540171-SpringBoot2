package com.hcltech.onlineshopping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank(message = "Product name is mandatory")
    private String productName;

    private String description;

    @NotNull(message = "Product price is mandatory")
    @Positive(message = "Product price must be greater than zero")
    private Double price;

    @NotNull(message = "Product quantity is mandatory")
    @Min(value = 0, message = "Product quantity cannot be negative")
    private Integer quantity;

    private String category;

    public Product() {
    }

    public Product(String productName, String description, Double price, Integer quantity, String category) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public Product(Long productId, String productName, String description, Double price, Integer quantity, String category) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

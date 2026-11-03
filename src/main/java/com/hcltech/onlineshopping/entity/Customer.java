package com.hcltech.onlineshopping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @NotBlank(message = "Customer name cannot be blank")
    private String customerName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotBlank(message = "Mobile number cannot be blank")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must contain exactly 10 digits")
    private String mobile;

    private String city;

    public Customer() {
    }

    public Customer(String customerName, String email, String password, String mobile, String city) {
        this.customerName = customerName;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.city = city;
    }

    public Customer(Long customerId, String customerName, String email, String password, String mobile, String city) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.city = city;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

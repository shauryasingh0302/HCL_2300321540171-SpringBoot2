-- ====================================================
-- Online Shopping Application - Database Creation Script
-- HCL Tech Training Practical Assessment
-- ====================================================

CREATE DATABASE IF NOT EXISTS online_shopping_db;
USE online_shopping_db;

-- 1. Customers Table
CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    mobile VARCHAR(10) NOT NULL,
    city VARCHAR(255)
);

-- 2. Products Table
CREATE TABLE IF NOT EXISTS products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    category VARCHAR(255)
);

-- 3. Carts Table (One cart per customer)
CREATE TABLE IF NOT EXISTS carts (
    cart_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT UNIQUE,
    created_date DATE,
    CONSTRAINT fk_cart_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- 4. Cart Items Table
CREATE TABLE IF NOT EXISTS cart_items (
    cart_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT,
    product_id BIGINT,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES carts(cart_id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- 5. Customer Orders Table
CREATE TABLE IF NOT EXISTS customer_orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT,
    order_date DATETIME,
    total_amount DOUBLE NOT NULL,
    order_status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- 6. Order Items Table
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_order_id BIGINT,
    product_id BIGINT,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    CONSTRAINT fk_order_item_order FOREIGN KEY (customer_order_id) REFERENCES customer_orders(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- ====================================================
-- Sample Seed Data (Optional for testing)
-- ====================================================

INSERT INTO products (product_name, description, price, quantity, category) VALUES
('iPhone 15', 'Apple iPhone 15 128GB Black', 79999.00, 50, 'Electronics'),
('Samsung Galaxy S24', 'Samsung Galaxy S24 256GB Onyx Black', 74999.00, 30, 'Electronics'),
('Sony WH-1000XM5', 'Wireless Noise Cancelling Headphones', 26990.00, 20, 'Audio'),
('Dell XPS 15', 'Dell XPS 15 Laptop Intel i7 16GB 512GB SSD', 135000.00, 15, 'Computers'),
('Nike Air Max', 'Nike Air Max Men Running Shoes', 8995.00, 40, 'Footwear');

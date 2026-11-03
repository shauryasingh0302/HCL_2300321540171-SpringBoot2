# Online Shopping Application

RESTful Online Shopping Application built with Spring Boot, Spring Data JPA, and MySQL for HCL Tech Training Practical Assessment.

---

## 1. Tech Stack

- **Language:** Java 17+ (Compatible with Java 17, 21, 27)
- **Framework:** Spring Boot 3.2.0 (Spring Web, Spring Data JPA, Spring Validation)
- **Persistence:** Hibernate 6.x, MySQL 8.x Connector, H2 In-Memory DB (for automated testing)
- **Boilerplate / Tools:** Lombok, Jakarta Validation (Hibernate Validator)
- **Testing:** JUnit 5, Spring Boot Starter Test, MockMvc (33 automated test cases passing)
- **Build Tool:** Apache Maven 3.8+

---

## 2. Project Setup & Prerequisites

### Prerequisites

1. **Java 17+** installed → verify with:
   ```bash
   java -version
   ```
2. **Maven 3.8+** installed → verify with:
   ```bash
   mvn -version
   ```
3. **MySQL 8.x** running on `localhost:3306` (or use the included H2 in-memory test suite)

### Database Setup

Run the SQL script located at `src/main/resources/schema.sql` or execute:

```sql
CREATE DATABASE IF NOT EXISTS online_shopping_db;
USE online_shopping_db;
```

> **Note:** The application uses `spring.jpa.hibernate.ddl-auto=update`, so all tables and constraints are also automatically created and updated by Hibernate upon application startup.

### Configuration

Edit `src/main/resources/application.properties` if your MySQL credentials differ:

```properties
spring.application.name=online-shopping
spring.datasource.url=jdbc:mysql://localhost:3306/online_shopping_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
server.port=8080
```

### Build & Run

```bash
# Build the project
mvn clean install

# Run automated tests (33 tests covering all business rules & validations)
mvn test

# Run the application
mvn spring-boot:run
```

The application starts at: **http://localhost:8080**

---

## 3. Database Schema

The application implements the 6 required entities with JPA entity mappings:

1. **Customer** (`customers` table):
   - `customerId` (Long, PK, Auto-increment)
   - `customerName` (String, Not Blank)
   - `email` (String, Unique, Valid Email)
   - `password` (String, Not Blank)
   - `mobile` (String, 10 Digits)
   - `city` (String)

2. **Product** (`products` table):
   - `productId` (Long, PK, Auto-increment)
   - `productName` (String, Mandatory)
   - `description` (String)
   - `price` (Double, > 0)
   - `quantity` (Integer, >= 0)
   - `category` (String)

3. **Cart** (`carts` table):
   - `cartId` (Long, PK, Auto-increment)
   - `customer_id` (FK to Customer, Unique – 1 cart per customer)
   - `createdDate` (LocalDate)
   - `cartItems` (OneToMany with CartItem)

4. **CartItem** (`cart_items` table):
   - `cartItemId` (Long, PK, Auto-increment)
   - `cart_id` (FK to Cart)
   - `product_id` (FK to Product)
   - `quantity` (Integer)
   - `price` (Double – calculated as product.price * quantity)

5. **CustomerOrder** (`customer_orders` table):
   - `orderId` (Long, PK, Auto-increment)
   - `customer_id` (FK to Customer)
   - `orderDate` (LocalDateTime)
   - `totalAmount` (Double)
   - `orderStatus` (String: `PLACED`, `CANCELLED`)
   - `orderItems` (OneToMany with OrderItem)

6. **OrderItem** (`order_items` table):
   - `orderItemId` (Long, PK, Auto-increment)
   - `customer_order_id` (FK to CustomerOrder)
   - `product_id` (FK to Product)
   - `quantity` (Integer)
   - `price` (Double)

---

## 4. API Endpoints Reference

### Task 1 – Customer Management APIs

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/customers/register` | Register customer |
| `GET` | `/api/customers` | Get all customers |
| `GET` | `/api/customers/{id}` | Get customer by ID |
| `PUT` | `/api/customers/{id}` | Update customer |
| `DELETE` | `/api/customers/{id}` | Delete customer |

### Task 2 – Product Management APIs

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/products` | Add product |
| `GET` | `/api/products` | Get all products |
| `GET` | `/api/products/{id}` | Get product by ID |
| `PUT` | `/api/products/{id}` | Update product |
| `DELETE` | `/api/products/{id}` | Delete product |
| `GET` | `/api/products/category/{category}` | Get products by category |
| `GET` | `/api/products/search?name={name}` | Search products by name |

### Task 3 – Shopping Cart Management

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/cart/add/{customerId}/{productId}` | Add product to cart (`?quantity=N` or JSON body) |
| `GET` | `/api/cart/{customerId}` | View customer's cart |
| `PUT` | `/api/cart/update/{cartItemId}` | Update cart quantity (`?quantity=N` or JSON body) |
| `DELETE` | `/api/cart/remove/{cartItemId}` | Remove product from cart |
| `DELETE` | `/api/cart/clear/{customerId}` | Clear entire cart |

### Order Management APIs

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/orders/place/{customerId}` | Place order from cart |
| `GET` | `/api/orders/{orderId}` | Get order by ID |
| `GET` | `/api/orders/customer/{customerId}` | View customer order history |
| `GET` | `/api/orders` | View all orders |
| `PUT` | `/api/orders/cancel/{orderId}` | Cancel eligible order (`PLACED` status) |

---

## 5. Demonstration: Successful & Failure Responses

### Task 1: Customer Management

#### 1. Register Customer — Success (`201 CREATED`)
**Request:**
```http
POST /api/customers/register
Content-Type: application/json

{
    "customerName": "John Doe",
    "email": "john.doe@example.com",
    "password": "Password@123",
    "mobile": "9876543210",
    "city": "Mumbai"
}
```
**Response (`201 CREATED`):**
```json
{
    "customerId": 1,
    "customerName": "John Doe",
    "email": "john.doe@example.com",
    "password": "Password@123",
    "mobile": "9876543210",
    "city": "Mumbai"
}
```

#### 2. Register Customer — Duplicate Email (`409 CONFLICT`)
**Response:**
```json
{
    "status": 409,
    "message": "Customer with email john.doe@example.com already exists",
    "timestamp": "2026-11-03T16:45:00"
}
```

#### 3. Register Customer — Validation Errors (`400 BAD REQUEST`)
**Request:**
```json
{
    "customerName": "",
    "email": "invalid-email",
    "password": "",
    "mobile": "12345",
    "city": "Mumbai"
}
```
**Response (`400 BAD REQUEST`):**
```json
{
    "status": 400,
    "message": "Validation failed",
    "timestamp": "2026-11-03T16:45:00",
    "errors": {
        "customerName": "Customer name cannot be blank",
        "email": "Invalid email format",
        "password": "Password cannot be blank",
        "mobile": "Mobile number must contain exactly 10 digits"
    }
}
```

---

### Task 2: Product Management

#### 1. Add Product — Success (`201 CREATED`)
**Request:**
```http
POST /api/products
Content-Type: application/json

{
    "productName": "iPhone 15",
    "description": "Apple iPhone 15 128GB Black",
    "price": 79999.00,
    "quantity": 50,
    "category": "Electronics"
}
```
**Response (`201 CREATED`):**
```json
{
    "productId": 1,
    "productName": "iPhone 15",
    "description": "Apple iPhone 15 128GB Black",
    "price": 79999.0,
    "quantity": 50,
    "category": "Electronics"
}
```

#### 2. Add Product — Validation Failure (`400 BAD REQUEST`)
**Request (Price <= 0, Negative Quantity, Blank Name):**
```json
{
    "productName": "",
    "price": -100.0,
    "quantity": -5,
    "category": "Electronics"
}
```
**Response (`400 BAD REQUEST`):**
```json
{
    "status": 400,
    "message": "Validation failed",
    "timestamp": "2026-11-03T16:45:00",
    "errors": {
        "productName": "Product name is mandatory",
        "price": "Product price must be greater than zero",
        "quantity": "Product quantity cannot be negative"
    }
}
```

#### 3. Search Products by Name — Success (`200 OK`)
**Request:**
```http
GET /api/products/search?name=phone
```
**Response (`200 OK`):**
```json
[
    {
        "productId": 1,
        "productName": "iPhone 15",
        "description": "Apple iPhone 15 128GB Black",
        "price": 79999.0,
        "quantity": 50,
        "category": "Electronics"
    }
]
```

---

### Task 3: Shopping Cart Management

#### 1. Add Product to Cart — Success (`201 CREATED`)
**Request:**
```http
POST /api/cart/add/1/1?quantity=2
```
**Response (`201 CREATED`):**
```json
{
    "cartId": 1,
    "customer": {
        "customerId": 1,
        "customerName": "John Doe",
        "email": "john.doe@example.com",
        "mobile": "9876543210",
        "city": "Mumbai"
    },
    "createdDate": "2026-11-03",
    "cartItems": [
        {
            "cartItemId": 1,
            "product": {
                "productId": 1,
                "productName": "iPhone 15",
                "price": 79999.0,
                "quantity": 50,
                "category": "Electronics"
            },
            "quantity": 2,
            "price": 159998.0
        }
    ],
    "cartTotal": 159998.0
}
```

#### 2. Add Same Product Again — Updates Quantity (`201 CREATED`)
**Request (add 1 more):**
```http
POST /api/cart/add/1/1?quantity=1
```
**Response:**
```json
{
    "cartId": 1,
    "cartItems": [
        {
            "cartItemId": 1,
            "quantity": 3,
            "price": 239997.0
        }
    ],
    "cartTotal": 239997.0
}
```

#### 3. Add Product Exceeding Stock — Failure (`400 BAD REQUEST`)
**Request (Product stock is 50, request 100):**
```http
POST /api/cart/add/1/1?quantity=100
```
**Response (`400 BAD REQUEST`):**
```json
{
    "status": 400,
    "message": "Insufficient stock for product: iPhone 15. Available: 50, Requested: 100",
    "timestamp": "2026-11-03T16:45:00"
}
```

---

### Order Management

#### 1. Place Order — Success (`201 CREATED`)
**Request:**
```http
POST /api/orders/place/1
```
**Response (`201 CREATED`):**
```json
{
    "orderId": 1,
    "customer": {
        "customerId": 1,
        "customerName": "John Doe"
    },
    "orderDate": "2026-11-03T16:45:30",
    "totalAmount": 239997.0,
    "orderStatus": "PLACED",
    "orderItems": [
        {
            "orderItemId": 1,
            "product": {
                "productId": 1,
                "productName": "iPhone 15"
            },
            "quantity": 3,
            "price": 239997.0
        }
    ]
}
```
*(Product stock is decremented in DB from 50 to 47, and cart is automatically cleared)*

#### 2. Cancel Order — Success (`200 OK`)
**Request:**
```http
PUT /api/orders/cancel/1
```
**Response (`200 OK`):**
```json
{
    "orderId": 1,
    "orderStatus": "CANCELLED",
    "totalAmount": 239997.0
}
```
*(Product stock is restored in DB from 47 back to 50)*

#### 3. Cancel Already Cancelled Order — Failure (`400 BAD REQUEST`)
**Request:**
```http
PUT /api/orders/cancel/1
```
**Response (`400 BAD REQUEST`):**
```json
{
    "status": 400,
    "message": "Only orders with status 'PLACED' can be cancelled. Current status: CANCELLED",
    "timestamp": "2026-11-03T16:45:00"
}
```

---

## 6. Automated Test Suite Summary

Run with:
```bash
mvn test
```

| Test Class | Test Count | Description |
|---|---|---|
| `CustomerControllerTest` | 8 tests | Tests registration, validation (name, email, mobile), duplicate check, CRUD |
| `ProductControllerTest` | 11 tests | Tests product creation, price/qty validations, search by name, category, CRUD |
| `CartControllerTest` | 7 tests | Tests add item, duplicate item quantity merge, stock validation, clear, update |
| `OrderControllerTest` | 6 tests | Tests place order, stock deduction, empty cart error, cancel order & stock restoration |
| `OnlineShoppingApplicationTests` | 1 test | Verifies Spring application context loads cleanly |
| **Total** | **33 tests** | **100% Passed (0 Failures, 0 Errors)** |

---

## 7. Submission Checklist

- [x] All 6 JPA entity classes mapped correctly with relationships
- [x] Task 1: Customer Management APIs with validations implemented
- [x] Task 2: Product Management APIs with search and validations implemented
- [x] Task 3: Shopping Cart Management with all business rules enforced
- [x] Order Management (place order, cancel order, stock deduction & restoration)
- [x] Global Exception Handling returning structured JSON responses
- [x] 33 automated tests covering successful and error flows
- [x] `schema.sql` database script provided
- [x] Comprehensive `README.md` with setup guide and endpoint demonstrations

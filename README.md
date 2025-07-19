# 🛠️ Altech - Java Backend Technical Assessment

A Spring Boot application for an electronics store checkout system with product management, discount deals, and shopping basket functionality.

## 📋 Requirements Implementation

### ✅ Admin Operations
- Create and manage products
- Add discount deals with expiration dates
- Remove products (soft delete)
- View all products and deals with pagination
- Update product stock quantities

### ✅ Customer Operations
- Browse products with filtering and pagination
- Add/remove items from shopping basket
- View active deals for products
- Calculate receipts with applied discounts
- Filter products by category, price range, and availability

### ✅ Transactional Integrity
- All operations are transactional to prevent partial updates
- Stock management with automatic decrement
- Deal expiration handling

## 🚀 Quick Start

### Prerequisites
- Java21aven3.6 Docker and Docker Compose (for containerized setup)

### Option 1: Docker (Recommended)
```bash
# Start the application with database
docker-compose up --build
```

The application will be available at: http://localhost:880## Option 2: Local Development
```bash
# Start PostgreSQL database (if not using Docker)
# Create database: interview_db

# Start the application
./mvnw spring-boot:run
```

## 🧪 Testing

### Automated Tests
The application includes comprehensive automated tests that verify all requirements:

- **Service Layer Tests**: Business logic validation
- **Repository Tests**: Data access layer testing
- **Integration Tests**: End-to-end flow verification
- **API Tests**: REST endpoint validation

### Run Tests
```bash
# Run all automated tests
./mvnw test
```

**Note**: If any requirement is not satisfied, the corresponding test will fail.

## 📡 API Endpoints

### Admin Endpoints (`/api/admin`)
- `POST /api/admin/products` - Create product
- `GET /api/admin/products` - Get all products (paginated)
- `DELETE /api/admin/products/{id}` - Delete product
- `POST /api/admin/deals` - Create deal
- `GET /api/admin/deals` - Get all deals (paginated)
- `POST /api/admin/deals/deactivate-expired` - Deactivate expired deals

### Customer Endpoints (`/api/customer`)
- `GET /api/customer/products` - Get all products (paginated)
- `POST /api/customer/products/filter` - Filter products
- `POST /api/customer/basket/items` - Add item to basket
- `DELETE /api/customer/basket/items/{productId}` - Remove item from basket
- `GET /api/customer/basket/receipt` - Calculate receipt

## 🗄️ Database

The application uses PostgreSQL with Flyway migrations. Database schema includes:
- Products table with stock management
- Deals table with expiration dates
- Baskets and basket items for shopping cart functionality

## 📦 Technology Stack

- Spring Boot 30.50.3
- Spring Data JPA
- PostgreSQL
- Flyway Migration
- Maven
- Docker & Docker Compose

## 📄 Documentation

- API Documentation: http://localhost:8080/swagger-ui.html (when running)
- This document is included in the repository as required 
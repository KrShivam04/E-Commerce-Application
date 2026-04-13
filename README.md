# 🛒 E-Commerce Backend API

A professional and scalable **Spring Boot based E-Commerce Backend** built to handle core online shopping operations such as authentication, product catalog management, cart handling, address management, and order processing.

This project solves the backend foundation required for an e-commerce platform by providing secure REST APIs, role-based access support, image handling for products, and database persistence for day-to-day commerce workflows.

## Project Overview

This backend is designed for modern e-commerce applications that need:

- Secure user authentication and authorization using **JWT**
- Product and category management for storefront operations
- Cart and order workflows for customer purchases
- Address management for delivery handling
- Admin-level APIs for maintaining catalog data
- API documentation support using **Swagger / OpenAPI**

It is suitable for backend-focused portfolios, Java full stack projects, and production-style learning implementations.

## Tech Stack

| Layer | Technologies |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot |
| Web | Spring Web |
| Security | Spring Security, JWT |
| Database | MySQL |
| ORM | Spring Data JPA, Hibernate |
| Validation | Jakarta Validation |
| Object Mapping | ModelMapper |
| Documentation | Springdoc OpenAPI, Swagger UI |
| Build Tool | Maven |
| Utilities | Lombok |
| Packaging | WAR |

## Features

- `User Authentication`
  - User registration
  - User login
  - JWT-based authentication
  - Sign out support
  - Role assignment support (`USER`, `SELLER`, `ADMIN`)

- `Category Management`
  - Create category
  - Update category
  - Delete category
  - Fetch paginated category list

- `Product Management`
  - Add product to category
  - Update product details
  - Delete product
  - Fetch all products with pagination and sorting
  - Filter products by category
  - Search products by keyword
  - Upload/update product image

- `Cart Management`
  - Add product to cart
  - View current user cart
  - Update product quantity in cart
  - Remove product from cart
  - Fetch all carts

- `Address Management`
  - Create address
  - Update address
  - Delete address
  - Fetch all addresses
  - Fetch user-specific addresses

- `Order Management`
  - Place order from cart
  - Attach address to order
  - Persist payment details
  - Reduce product stock after order placement
  - Clear cart after successful checkout

- `Admin Controls`
  - Product create/update/delete endpoints
  - Category delete endpoint
  - Role-based architecture ready for admin workflows

## API Endpoints

Base URL: `http://localhost:5000`

### Authentication

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/signin` | Authenticate user and issue JWT cookie |
| POST | `/api/auth/signout` | Clear authentication cookie |
| GET | `/api/auth/username` | Get current authenticated username |
| GET | `/api/auth/user` | Get current authenticated user details |

### Categories

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/public/categories` | Get all categories with pagination and sorting |
| POST | `/api/public/categories` | Create a category |
| PUT | `/api/public/categories/{categoryId}` | Update category details |
| DELETE | `/api/admin/category/{categoryId}` | Delete a category |

### Products

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/public/products` | Get all products with filters, pagination, and sorting |
| GET | `/api/public/categories/{categoryId}/products` | Get products by category |
| GET | `/api/public/products/keyword/{keyword}` | Search products by keyword |
| POST | `/api/admin/categories/{categoryId}/product` | Add product under a category |
| PUT | `/api/admin/products/{productId}` | Update product details |
| DELETE | `/api/admin/products/{productId}` | Delete a product |
| PUT | `/api/products/{productId}/image` | Upload or update product image |

### Cart

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/carts/products/{productId}/quantity/{quantity}` | Add product to cart |
| GET | `/api/carts` | Get all carts |
| GET | `/api/carts/users/cart` | Get logged-in user's cart |
| PUT | `/api/cart/product/{productId}/quantity/{operation}` | Increase or decrease cart quantity |
| DELETE | `/api/carts/{cartId}/product/{productId}` | Remove product from cart |

### Addresses

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/addresses` | Create a new address |
| GET | `/api/addresses` | Get all addresses |
| GET | `/api/addresses/{addressId}` | Get address by ID |
| GET | `/api/user/addresses` | Get addresses for logged-in user |
| PUT | `/api/addresses/{addressId}` | Update address |
| DELETE | `/api/addresses/{addressId}` | Delete address |

### Orders

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/order/users/payments/{paymentMethod}` | Place an order using cart items and payment details |

### API Documentation

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/swagger-ui/index.html` | Swagger UI |
| GET | `/v3/api-docs` | OpenAPI specification |

## Folder Structure
```
e-comm/
├── images/
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/project/
│   │   │   ├── Config/
│   │   │   ├── Controller/
│   │   │   ├── Exception/
│   │   │   ├── Payload/
│   │   │   ├── Repository/
│   │   │   ├── Security/
│   │   │   │   ├── jwt/
│   │   │   │   ├── request/
│   │   │   │   ├── response/
│   │   │   │   └── services/
│   │   │   ├── model/
│   │   │   ├── service/
│   │   │   ├── util/
│   │   │   ├── ECommApplication.java
│   │   │   └── ServletInitializer.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/ecommerce/project/
├── .mvn/
├── mvnw
├── mvnw.cmd
└── pom.xml
```


## Access the Application

- API Base URL: `http://localhost:5000`
- Swagger UI: `http://localhost:5000/swagger-ui/index.html`

## Environment Variables

| Variable | Description |
| --- | --- |
| `PORT` | Application port |
| `DB_URL` | MySQL database connection URL |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | Secret key used for JWT generation |
| `JWT_EXPIRATION_MS` | JWT expiration time in milliseconds |
| `JWT_COOKIE_NAME` | Authentication cookie name |
| `FRONTEND_URL` | Allowed frontend origin |
| `IMAGE_UPLOAD_PATH` | Local folder for uploaded images |
| `IMAGE_BASE_URL` | Base URL used to serve images |

## Usage

- Use **Postman**, **Swagger UI**, or any frontend client to test the APIs.
- Start by registering a user through `/api/auth/signup`.
- Login using `/api/auth/signin` to receive the authentication cookie/token details.
- Use authenticated endpoints for cart, address, and order workflows.
- Use admin-focused endpoints to manage products and categories.
- Swagger UI is available for quick API testing and endpoint discovery.


## Author

**Shivam Kumar**  
Java Backend Developer  
Email: `jhashivamjha56789@gmail.com`

## License

This project is licensed under the **MIT License**.  


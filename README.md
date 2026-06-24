## 📌 About the Project

Expense Tracker is a personal finance management application that allows users to track income and expenses, organize transactions, and monitor their account balance.

The project was created as a portfolio application to demonstrate backend development skills using Java and Spring technologies.

### ✨ Key Features

* User registration and authentication
* Wallet management
* Income and expense tracking
* Transaction categorization
* Custom transaction types
* Balance calculation


## 🛠 Tech Stack

### Development

* Java 21
* Spring Boot 3
* Spring Data
* Spring Security
* MySQL
* Lombok
* Maven 4.x

### Testing

* JUnit 5
* Mockito
* Spring Test
* Testcontainers


## 🚀 Getting Started

### Prerequisites

* Java 21 installed
* MySQL running locally
* IntelliJ IDEA (recommended)

### Database setup

1. Start your local MySQL server
2. Create a database (name should match configuration in `application.properties`)

### Running the application

1. Open the project in IntelliJ IDEA
2. Import Maven dependencies
3. Run the main application class (`@SpringBootApplication`) directly from IntelliJ
4. The application will start on the default port configured in `application.properties`


## 🔐 Security

The application uses an authentication mechanism based on JWT (JSON Web Token).

* No role-based authorization (single-user level access)


## 📡 API Overview

The application is built as a RESTful API using a simple CRUD-oriented architecture.

### Main Modules

* **Authorization** 
* **Wallet** 
* **Financial Transaction**
* **Financial Transaction Category** 
* **File** 

### Architecture

* REST API design
* CRUD-based operations for core resources
* Clear separation of concerns between modules
* Stateless authentication using JWT


## Testing

* **Unit Tests** 
* **Web Layer Tests** 
* **Integration Tests** 






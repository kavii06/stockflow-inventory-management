# StockFlow Inventory Management System

StockFlow is an inventory management web application that I developed for my PUSL2024 Software Engineering 2 referral coursework. The system allows products and categories to be managed while also providing a simple ordering process.

## Student Details

* Name: D.B.K.H. Jayalath
* Student ID: 10965397
* Degree Programme: Software Engineering
* Project Type: Individual

## What the System Can Do

* Add, view, edit and delete categories
* Add, view, edit and delete products
* Search for products
* View products through a catalogue
* Check available stock
* Create and confirm orders
* Automatically reduce stock after an order
* Prevent orders when there is insufficient stock
* Provide REST API responses
* Display validation and error messages
* Provide separate ADMIN and USER access

## Software and Technologies

The project was developed using:

* Java 21
* Spring Boot
* Spring MVC
* Spring Data JPA
* Spring Security
* Thymeleaf
* Hibernate
* MySQL
* Maven
* HTML and CSS
* IntelliJ IDEA
* MySQL Workbench

## Database Setup

First, create the following database in MySQL:

```
CREATE DATABASE inventory_management;
```

The MySQL password is not stored directly in the project. It must be added as an environment variable called `DB_PASSWORD`.

In IntelliJ IDEA, open:

```
Run > Edit Configurations > Environment variables
```

Then add the password in this format:

```
DB_PASSWORD=your_mysql_password
```

Replace `your_mysql_password` with the password used for the local MySQL server.

## How to Run the Project

1. Install Java 21 and MySQL.
2. Create the `inventory_management` database.
3. Add the `DB_PASSWORD` environment variable in IntelliJ IDEA.
4. Open the project in IntelliJ IDEA.
5. Wait for Maven to download the dependencies.
6. Run the `InventoryManagementApplication` class.
7. Open `http://localhost:8080` in a browser.

## Login Details

Administrator account:

* Username: `admin`
* Password: `admin123`

Standard user account:

* Username: `user`
* Password: `user123`

These accounts are provided for testing and demonstrating the coursework.

## User Roles

The ADMIN account can manage categories and products. The USER account can browse the catalogue and place orders. Spring Security is used to protect the pages and restrict access according to the account role.

## Order Processing

The ordering process uses Spring transaction management. When an order is confirmed, the system checks the available quantity, updates the product stock and saves the order. These actions are completed as one transaction so the database remains consistent if an operation fails.

## REST API

Product data can be viewed through:

```
http://localhost:8080/api/products
```

The API also returns a structured error response when a requested product cannot be found.

## Author

D.B.K.H. Jayalath
Student ID: 10965397

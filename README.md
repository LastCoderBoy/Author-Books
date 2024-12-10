**Book Management System**
A simple book management system built with Spring Boot and JDBC, providing functionality to manage books, authors, and book-author relationships. This system allows users to create, update, and retrieve book records.

**Features**
  * Create: Add new books with title, ISBN, and author.
  * Update: Edit existing books' title or author.
  * Read: View a list of all books, including their ISBN and author details.
  * Database: Uses PostgreSQL for data storage, with JDBC for communication.

**Technologies Used**
  Backend: Spring Boot
  Database: PostgreSQL (via JDBC)
  Frontend: HTML, JavaScript (Axios for HTTP requests)

**Installation**
Clone the repository:

**git clone https://github.com/LastCoderBoy/Author-Books.git**

Install dependencies:
Ensure you have PostgreSQL running.
Configure your application.properties file for the database connection.

Run the Spring Boot application:

**mvn spring-boot:run**

Open your browser and navigate to http://localhost:8080 to access the application.

Endpoints
  * GET /books - Retrieve all books
  * POST /create - Create a new book
  * PUT /update/{isbn} - Update an existing book by ISBN

Contributing
Feel free to fork the repo, submit issues, or create pull requests for improvements.

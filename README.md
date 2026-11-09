A REST API for a simple banking system: users, bank accounts, deposits,
withdrawals and transaction history.

**Tech:** Java 17 · Spring Boot 4 · Spring Data JPA · H2 · JUnit 5 · Mockito · MockMvc
**Highlights:** 8 REST endpoints · layered architecture with DTOs and mappers ·
global exception handling · 28 automated tests

### Quick start
./mvnw spring-boot:run      # then open http://localhost:8081
./mvnw test                 # runs all tests
# Banking System REST API

A Java Spring Boot banking application built as a portfolio project.

This project started as a console-based Java banking system and was gradually converted into a Spring Boot REST API. It demonstrates object-oriented programming, layered architecture, DTOs, request validation, exception handling, JPA persistence, automated testing, Git/GitHub workflow, and REST API design.

## Technologies Used

- Java 17
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data JPA
- H2 Database
- Jakarta Bean Validation
- Maven
- JUnit
- Mockito
- MockMvc
- Postman
- Git
- GitHub

## Main Features

- Create users
- Get all users
- Get user by ID
- Create bank accounts for users
- Get all accounts for a user
- Deposit money into an account
- Withdraw money from an account
- Record transaction history
- Get transactions for an account
- Validate incoming requests
- Handle custom business exceptions
- Use request DTOs and response DTOs to control API data
- Use mappers to convert entities into response DTOs
- Persist users, accounts, and transactions using JPA
- Test service-layer business logic with JUnit and Mockito
- Test controller endpoints with MockMvc

## Project Evolution: Console App to Spring Boot REST API

The project was originally built as a Java console application. The banking domain and business operations already existed, but the user interacted with the application through a `Main` class and console input/output.

The migration to Spring Boot did not replace the banking logic. Instead, it changed how the application receives requests, returns responses, and stores data.

### 1. Console Application

The original flow was approximately:

```text
User
  ↓
Console / Main
  ↓
BankService
  ↓
Java domain objects and collections
```

The core domain consisted of classes such as:

- `User`
- `BankAccount`
- `Transaction`
- `TransactionType`

`BankService` handled operations such as creating users and accounts, depositing money, withdrawing money, and viewing transaction history.

### 2. REST Controllers Replaced the Console Interface

When the project was migrated to Spring Boot, the console `Main` class was no longer the main way to interact with the application.

The new entry point became HTTP requests handled by REST controllers:

```text
Client / Postman
      ↓
HTTP Request
      ↓
Controller
      ↓
Service
```

For example, a deposit can now be requested with:

```http
POST /bank/accounts/1001/deposit
```

and a JSON request body:

```json
{
  "amount": 100
}
```

The important idea is that the banking operation is still a deposit. Only the way the application receives the instruction changed.

### 3. DTOs Were Added for API Communication

Request DTOs were introduced to represent incoming JSON data:

- `CreateUserRequest`
- `CreateAccountRequest`
- `AmountRequest`

Response DTOs were introduced to control the data returned to clients:

- `UserDTO`
- `AccountDTO`
- `TransactionDTO`

This created a clear API boundary:

```text
JSON
  ↓
Request DTO
  ↓
Controller
  ↓
Service
```

and for responses:

```text
Entity
  ↓
Mapper
  ↓
Response DTO
  ↓
JSON
```

### 4. Validation Was Added at the HTTP Boundary

Jakarta Bean Validation is used to reject invalid request data before it reaches the service layer.

For example:

```java
@Positive(message = "Amount must be greater than zero.")
private double amount;
```

The controller uses `@Valid` to trigger validation.

The service layer still contains its own business-rule checks because it is responsible for protecting the business logic even if it is called from somewhere other than a validated controller.

```text
DTO validation = protects HTTP input
Service validation = protects business rules
```

### 5. JPA Repositories Added Database Persistence

The domain objects were converted into JPA entities and repositories were added for database access.

The application flow became:

```text
Client / Postman
      ↓
Controller
      ↓
Service
      ↓
Repository
      ↓
H2 Database
```

Spring Data JPA repositories are responsible for operations such as finding and saving users, accounts, and transactions.

### 6. Global Exception Handling Added Clean API Errors

Custom exceptions are thrown when business rules fail, while `GlobalExceptionHandler` converts validation and application exceptions into clean HTTP responses.

Example flow:

```text
Business rule fails
      ↓
Custom exception
      ↓
GlobalExceptionHandler
      ↓
HTTP status + JSON error response
```

### 7. Automated Tests Were Added

The project was expanded with automated tests:

- JUnit and Mockito test service-layer business logic
- MockMvc tests controller endpoints, HTTP status codes, JSON responses, and validation behavior

This replaced much of the manual testing that was necessary in the original console version.

### Migration Memory Rule

```text
Same banking domain and business logic
            +
new HTTP interface
            +
database persistence
            +
validation and error handling
            +
automated tests
            =
Spring Boot REST API
```

## Project Structure

```text
com.tudor.bank
├── controller
│   ├── UserController
│   ├── AccountController
│   └── TransactionController
├── dto
│   ├── UserDTO
│   ├── AccountDTO
│   ├── TransactionDTO
│   ├── CreateUserRequest
│   ├── CreateAccountRequest
│   └── AmountRequest
├── exception
│   ├── GlobalExceptionHandler
│   ├── UserNotFoundException
│   ├── AccountNotFoundException
│   ├── DuplicateUserException
│   ├── DuplicateAccountException
│   ├── InvalidAmountException
│   └── InsufficientBalanceException
├── mapper
│   ├── UserMapper
│   ├── AccountMapper
│   └── TransactionMapper
├── model
│   ├── User
│   ├── BankAccount
│   ├── Transaction
│   └── TransactionType
├── repository
│   ├── UserRepository
│   ├── BankAccountRepository
│   └── TransactionRepository
├── service
│   └── BankService
└── BankApplication
```

## Architecture

The project uses a layered structure:

- **Controller layer** handles HTTP requests and responses.
- **Service layer** contains the business logic.
- **Repository layer** communicates with the database.
- **Model layer** contains JPA entities.
- **DTO layer** controls request and response data.
- **Mapper layer** converts entities to response DTOs.
- **Exception layer** handles custom errors and validation responses.
- **Test layer** verifies service logic and controller endpoints.

### Request Flow Example: Deposit

```text
POST /bank/accounts/{accountNumber}/deposit
                  ↓
            AccountController
                  ↓
        JSON → AmountRequest
                  ↓
         Jakarta Validation
                  ↓
        BankService.deposit()
                  ↓
        Find account by number
                  ↓
          Update balance
                  ↓
       Add DEPOSIT transaction
                  ↓
     BankAccountRepository.save()
                  ↓
        Return BankAccount entity
                  ↓
            AccountMapper
                  ↓
             AccountDTO
                  ↓
            JSON response
```

## API Endpoints

### Users

| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| POST | `/bank/users` | Create a new user |
| GET | `/bank/users` | Get all users |
| GET | `/bank/users/{id}` | Get user by ID |

### Accounts

| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| POST | `/bank/users/{userId}/accounts` | Create an account for a user |
| GET | `/bank/users/{userId}/accounts` | Get all accounts for a user |
| POST | `/bank/accounts/{accountNumber}/deposit` | Deposit money into an account |
| POST | `/bank/accounts/{accountNumber}/withdraw` | Withdraw money from an account |

### Transactions

| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| GET | `/bank/accounts/{accountNumber}/transactions` | Get transaction history for an account |

## Example Requests

### Create User

```http
POST /bank/users
```

Request body:

```json
{
  "id": 1,
  "name": "Tudor"
}
```

Example response:

```json
{
  "id": 1,
  "name": "Tudor"
}
```

### Get All Users

```http
GET /bank/users
```

Example response:

```json
[
  {
    "id": 1,
    "name": "Tudor"
  }
]
```

### Get User by ID

```http
GET /bank/users/1
```

Example response:

```json
{
  "id": 1,
  "name": "Tudor"
}
```

### Create Account for User

```http
POST /bank/users/1/accounts
```

Request body:

```json
{
  "accountNumber": 1001,
  "balance": 500
}
```

Example response:

```json
{
  "accountNumber": 1001,
  "balance": 500.0
}
```

### Get Accounts for User

```http
GET /bank/users/1/accounts
```

Example response:

```json
[
  {
    "accountNumber": 1001,
    "balance": 500.0
  }
]
```

### Deposit Money

```http
POST /bank/accounts/1001/deposit
```

Request body:

```json
{
  "amount": 100
}
```

Example response:

```json
{
  "accountNumber": 1001,
  "balance": 600.0
}
```

### Withdraw Money

```http
POST /bank/accounts/1001/withdraw
```

Request body:

```json
{
  "amount": 50
}
```

Example response:

```json
{
  "accountNumber": 1001,
  "balance": 550.0
}
```

### Get Transaction History

```http
GET /bank/accounts/1001/transactions
```

Example response:

```json
[
  {
    "type": "DEPOSIT",
    "amount": 100.0
  },
  {
    "type": "WITHDRAWAL",
    "amount": 50.0
  }
]
```

## Validation Examples

The API validates incoming requests using Jakarta Bean Validation.

### Invalid User Request

```json
{
  "id": 0,
  "name": ""
}
```

Example response:

```json
{
  "id": "User id must be greater than zero.",
  "name": "Name cannot be blank."
}
```

### Invalid Account Request

```json
{
  "accountNumber": 0,
  "balance": -500
}
```

Example response:

```json
{
  "accountNumber": "Account number must be greater than zero.",
  "balance": "Initial balance cannot be negative."
}
```

### Invalid Deposit or Withdraw Request

```json
{
  "amount": -100
}
```

Example response:

```json
{
  "amount": "Amount must be greater than zero."
}
```

## Business Rules

- User IDs must be unique.
- Account numbers must be unique.
- User ID must be greater than zero.
- Account number must be greater than zero.
- Initial account balance cannot be negative.
- Deposit amount must be greater than zero.
- Withdrawal amount must be greater than zero.
- Withdrawal amount cannot be greater than the account balance.
- Each account belongs to one user.
- Each transaction belongs to one account.

## Database

The project uses an H2 in-memory database with Spring Data JPA.

Main entities:

- `User`
- `BankAccount`
- `Transaction`

Relationships:

- One `User` can have many `BankAccount` objects.
- One `BankAccount` belongs to one `User`.
- One `BankAccount` can have many `Transaction` objects.
- One `Transaction` belongs to one `BankAccount`.

Because the configured database URL uses `jdbc:h2:mem:bankdb`, application data exists while the application is running and is lost when the application stops.

## Testing

The project includes automated tests for both the service layer and the controller layer.

### Test Types

- **Service unit tests** using JUnit and Mockito
- **Controller tests** using MockMvc
- Validation tests for invalid request bodies
- Business rule tests for duplicate users, duplicate accounts, invalid amounts, and insufficient balance

### Tested Areas

- User creation
- Getting all users
- Getting a user by ID
- Finding an account by account number
- Account creation
- Getting accounts for a user
- Deposits
- Withdrawals
- Transaction history
- Request validation
- Custom exception handling

### Running Tests

On Windows:

```cmd
mvnw.cmd test
```

On macOS/Linux:

```bash
./mvnw test
```

The tests can also be run directly from IntelliJ IDEA or Eclipse using the JUnit test runner.

## How to Run

1. Clone the repository.
2. Open the project in IntelliJ IDEA or Eclipse.
3. Make sure Maven dependencies are loaded.
4. Run `BankApplication`.
5. Test the API using Postman.

Default local URL:

```text
http://localhost:8081
```

Depending on your `application.properties`, the port may be different.

Example:

```properties
server.port=8081
```

## Example Application Properties

```properties
spring.application.name=bank
server.port=8081

spring.datasource.url=jdbc:h2:mem:bankdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## What I Learned

Through this project, I practiced:

- Building a Java application using object-oriented programming
- Migrating a console Java project to Spring Boot
- Understanding how console interaction can be replaced by REST controllers
- Creating REST controllers and HTTP endpoints
- Using service-layer architecture
- Creating request DTOs and response DTOs
- Using mapper classes to convert entities into API responses
- Handling validation with Jakarta Bean Validation
- Creating custom exceptions
- Using global exception handling
- Working with Spring Data JPA repositories
- Mapping entity relationships with JPA
- Avoiding JSON recursion with Jackson annotations
- Testing REST endpoints manually with Postman
- Writing service-layer unit tests with JUnit and Mockito
- Writing controller tests with MockMvc
- Running tests from the IDE and from the command line
- Using Git and GitHub for version control
- Improving project structure step by step
- Understanding patterns and application flow instead of memorizing individual lines of code

## Future Improvements

Possible future improvements:

- Add more integration tests with a real test database
- Add Swagger/OpenAPI documentation
- Add authentication and authorization
- Replace H2 with PostgreSQL or MySQL
- Add Docker support
- Add pagination for larger datasets
- Add GitHub Actions to run tests automatically
- Use BigDecimal instead of double for money, to avoid rounding errors
- Make withdrawals safe under concurrent requests (@Transactional with optimistic or pessimistic locking)
- Let the database generate user and account IDs instead of the client sending them

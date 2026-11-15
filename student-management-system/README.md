# Student Management System

REST API built with **Java 17, Spring Boot 3, Spring Data JPA and MySQL 8**.

## Prerequisites
- JDK 17+
- Maven 3.8+
- MySQL 8.x running on `localhost:3306`

## Setup
1. Create the database (optional - the JDBC URL also has `createDatabaseIfNotExist=true`):
   ```sql
   CREATE DATABASE student_db;
   ```
2. Set credentials (or edit `src/main/resources/application.properties`):
   ```bash
   export DB_USERNAME=root
   export DB_PASSWORD=your_password
   ```
   On Windows (PowerShell): `$env:DB_USERNAME="root"; $env:DB_PASSWORD="your_password"`
3. Run:
   ```bash
   mvn spring-boot:run
   ```
4. Open Swagger UI: http://localhost:3031/swagger-ui.html
5. Run unit tests: `mvn test`

## Endpoints (base path `/api/v1`)

| Method | URL | Description |
|--------|-----|-------------|
| POST   | /courses        | Create course |
| GET    | /courses        | List courses |
| GET    | /courses/{id}   | Get course |
| PUT    | /courses/{id}   | Update course |
| DELETE | /courses/{id}   | Delete course (only if it has no students) |
| POST   | /students       | Create student |
| GET    | /students       | List students: `page, size, sortBy, direction, keyword, courseId` |
| GET    | /students/{id}  | Get student |
| PUT    | /students/{id}  | Update student |
| DELETE | /students/{id}  | Delete student |

## Quick test with cURL
```bash
curl -X POST http://localhost:3031/api/v1/courses \
  -H "Content-Type: application/json" \
  -d '{"courseCode":"BCA101","name":"Bachelor of Computer Applications","description":"3-year UG","durationInMonths":36}'

curl -X POST http://localhost:3031/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Aarav","lastName":"Sharma","email":"aarav.sharma@example.com","phone":"9876543210","dateOfBirth":"2004-05-17","gender":"MALE","address":"Sri Ganganagar","enrollmentDate":"2024-07-01","courseId":1}'

curl "http://localhost:3031/api/v1/students?keyword=sharma&sortBy=firstName&direction=asc&page=0&size=5"
```

## Project structure
```
src/main/java/com/example/sms
 ├── controller   (REST endpoints)
 ├── service      (business logic, transactions)
 ├── repository   (Spring Data JPA)
 ├── model        (JPA entities)
 ├── dto          (request/response records)
 └── exception    (custom exceptions + global handler)
```

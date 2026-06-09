# RevTalent Employee Service

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://jdk.java.net/21/)


The **Employee Service** is a core microservice in the **RevTalent** HRMS ecosystem. It manages all aspects of organizational directories, departments, reporting hierarchies, attendance tracking (check-in/check-out), company policies, peer-to-peer recognition (Kudos), and holiday lists.

---

##  Features

- **Employee Profiles & Directories**: Complete management of employee details, personal information updates, and status.
- **Reporting Hierarchy**: Built-in hierarchy retrieval for listing managers and direct reportees.
- **Attendance Management**: Daily check-in/check-out logs, real-time status tracking, and work hour calculations.
- **Peer Recognition (Kudos)**: A system allowing employees to send peer-to-peer appreciation notes.
- **Department Administration**: Manage company departments and heads of departments.
- **Policy & Document Hub**: Central repository for sharing company rules and compliance policies.
- **Holiday & Announcements**: Calendar listings of holidays and global company news.

---

##  Tech Stack & Dependencies

- **Framework**: Spring Boot (v3.3.5)
- **Language**: Java 21
- **Database**: MySQL 8.0 (JPA/Hibernate)
- **Registry & Config**: Spring Cloud Config Client, Netflix Eureka Client
- **Security**: Spring Security & JJWT (JSON Web Token validation)
- **Utilities**: Lombok, Maven, Jacoco (Test coverage check minimum 80%)

---

##  Database Schema (Entity Model)

The service maps the following models to the database:
- **`Employee`**: Core entity containing designation, joining details, profile picture URL, manager relationship (self-referencing), and link to department.
- **`Users`**: Holds login credentials, registration email, verified status, OTP codes, and roles.
- **`Department`**: Organizational units led by department heads.
- **`Attendance`**: Timecards tracking `checkInTime`, `checkOutTime`, and associated `Employee`.
- **`Kudos`**: Keeps records of appreciation notes sent between sender and receiver employees.
- **`Holiday`**: National/company holidays containing dates and titles.
- **`Policy`**: Documents and PDFs representing HR rules.

## Development Setup

### Prerequisite Environment Variables
Ensure the following variables are set or configured through the **Config Server**:
```bash
EUREKA_URL=http://localhost:8762/eureka/
CONFIG_SERVER_URL=http://localhost:8888
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3307/revtalent_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
JWT_SECRET=your_jwt_secret_key_here

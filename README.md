# Alumni Networking & Tracking Platform — Backend

A secure and scalable REST API backend for the **Alumni Networking & Tracking Platform**, developed to support alumni engagement, mentorship, events, announcements, community interaction, and institutional alumni management.

The backend provides authentication, role-based authorization, alumni management, mentorship verification, event management, community features, and data management through RESTful APIs.

## 🚀 Project Overview

The Alumni Networking & Tracking Platform is a web-based system designed to provide institutions with a centralized platform for managing and engaging with their alumni community.

This repository contains the **backend implementation** built using **Spring Boot**, with **MySQL** for persistent data storage and **Redis** for caching and performance optimization.

The backend is responsible for:

* User authentication and authorization
* Alumni registration and profile management
* Role-based access control
* Alumni directory and batch-wise tracking
* Mentorship management
* Event management
* Community forum functionality
* Announcements
* Secure API communication
* Database management
* Caching for improved performance

## 🛠️ Technology Stack

| Technology      | Purpose                                    |
| --------------- | ------------------------------------------ |
| Java            | Backend programming language               |
| Spring Boot     | Backend framework                          |
| Spring Security | Authentication and authorization           |
| JWT             | Token-based authentication                 |
| Google OAuth    | Google-based authentication                |
| MySQL           | Relational database                        |
| Redis           | Caching and performance optimization       |
| Maven           | Dependency management and build automation |
| REST API        | Communication between frontend and backend |
| Postman         | API testing                                |

## 🏗️ Architecture

The backend follows a layered architecture to maintain separation of concerns and make the application easier to maintain and extend.

```text
                    React Frontend
                          │
                          │ REST API
                          ▼
                ┌─────────────────────┐
                │     Spring Boot     │
                │      Backend        │
                └──────────┬──────────┘
                           │
              ┌────────────┴────────────┐
              │                         │
              ▼                         ▼
       Spring Security              REST APIs
              │
       JWT / Google OAuth
              │
              ▼
        ┌─────────────┐
        │   MySQL     │
        │  Database   │
        └─────────────┘
              │
              │
              ▼
        ┌─────────────┐
        │    Redis    │
        │    Cache    │
        └─────────────┘
```

## 📂 Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── ...
│   │
│   └── resources/
│       ├── application.properties
│       └── ...
│
└── test/
    └── ...
```

The backend follows a modular structure with components such as:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

### Main Layers

**Controller Layer**

* Handles incoming HTTP requests
* Provides REST API endpoints
* Returns responses to the frontend

**Service Layer**

* Contains business logic
* Processes application operations
* Handles validation and business rules

**Repository Layer**

* Communicates with the MySQL database
* Performs database operations using Spring Data JPA

**Security Layer**

* Handles authentication and authorization
* Implements JWT-based security
* Supports Google OAuth
* Restricts resources according to user roles

## 🔐 Authentication & Security

The backend implements multiple security mechanisms to protect user data and application resources.

### JWT Authentication

JWT tokens are used for secure, stateless authentication between the frontend and backend.

```text
User Login
    ↓
Authentication
    ↓
JWT Token Generated
    ↓
Token Sent to Client
    ↓
Token Included in API Requests
    ↓
Spring Security Validates Token
    ↓
Access Granted / Denied
```

### Google OAuth

Users can authenticate using their Google account through Google OAuth integration.

### Role-Based Access Control

Different users have different permissions based on their roles.

| Role   | Access                                                                                                    |
| ------ | --------------------------------------------------------------------------------------------------------- |
| Alumni | Profile, directory, mentorship, forum, events, announcements                                              |
| Admin  | Alumni management, event management, mentorship verification, announcements and administrative operations |

## 👥 Core Features

### Alumni Management

* Alumni registration and login
* Alumni profile management
* Alumni directory
* Batch-wise alumni tracking
* Alumni information management

### 🔗 Mentorship

The platform provides a structured mentorship system where alumni can participate as mentors based on their professional roles and experience.

Mentor eligibility can be verified by an administrator before the mentorship functionality is enabled.

### 📅 Event Management

Administrators can manage alumni-related events through the backend.

Features include:

* Event creation
* Event updates
* Event management
* Event information retrieval
* Alumni event participation

### 💬 Community Forum

The backend supports community interaction between alumni through forum functionality.

Alumni can:

* Create discussions
* Share information
* Interact with the alumni community
* Exchange professional and academic insights

### 📢 Announcements

Administrators can publish announcements to communicate important information to the alumni community.

### ⚡ Redis Caching

Redis is used to cache frequently accessed data and reduce unnecessary database queries.

This helps improve:

* API response time
* Application performance
* Database efficiency
* Scalability

## 🗄️ Database

The application uses **MySQL** as its primary relational database.

The database stores information related to:

* Users
* Alumni profiles
* Roles
* Events
* Mentorship
* Forum/community content
* Announcements
* Other platform-related data

## 🔄 API Communication

The backend exposes RESTful APIs that are consumed by the React frontend.

```text
React Frontend
      │
      │ HTTP Requests
      ▼
Spring Boot REST API
      │
      ├── Authentication
      ├── Alumni Management
      ├── Events
      ├── Mentorship
      ├── Forum
      └── Announcements
      │
      ▼
MySQL / Redis
```

## 🧪 API Testing

The APIs can be tested using **Postman**.

Testing includes:

* Authentication APIs
* User management APIs
* Alumni APIs
* Event APIs
* Mentorship APIs
* Forum APIs
* Announcement APIs
* Authorization and protected endpoints

## ⚙️ Setup & Installation

### Prerequisites

Make sure the following are installed:

* Java 17 or later
* Maven
* MySQL
* Redis
* Git

### 1. Clone the Repository

```bash
git clone <YOUR-BACKEND-REPOSITORY-URL>
cd <BACKEND-PROJECT-FOLDER>
```

### 2. Configure MySQL

Create a MySQL database for the project.

Update the database configuration in:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/alumni_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 3. Configure Redis

Make sure Redis is running locally and configure the Redis connection according to your project configuration.

### 4. Configure Authentication

Add the required JWT and Google OAuth configuration to your environment or application configuration.

**Do not commit passwords, API keys, client secrets, JWT secrets, or other sensitive credentials to GitHub.**

### 5. Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

Or build and run the project:

```bash
mvn clean install
java -jar target/<application-name>.jar
```

The backend will then be available on the configured Spring Boot port.

## 🔗 Related Repository

This repository contains the **backend** of the Alumni Networking & Tracking Platform.

**Frontend:**
`<[YOUR-FRONTEND-REPOSITORY-URL](https://github.com/oviyashree5/Alumni-Networking-Portal/tree/main)>`

**Backend:**
`<https://github.com/oviyashree5/Alumni-Networking-and-Engaging-Platform/new/main?filename=README.md>`

The frontend and backend are maintained as separate repositories and work together through REST APIs.

## 👩‍💻 Project

**Alumni Networking & Tracking Platform for Institutional Engagement**

Developed as a collaborative academic project to improve alumni engagement, communication, mentorship, event participation, and institutional alumni management.

### Backend Technologies

**Java • Spring Boot • Spring Security • JWT • Google OAuth • MySQL • Redis • Maven • REST API**

---

⭐ If you find this project useful, feel free to explore the repository and the related frontend project.

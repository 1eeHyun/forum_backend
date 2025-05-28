# Forum Backend (Spring Boot)

This is the backend of a forum application built with Java Spring Boot. It handles user authentication, post creation, comment management, notifications, community features, and more. It also uses Redis via Docker for caching recent views and post history.

## Table of Contents
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [Features](#features)
- [API Endpoints Overview](#api-endpoints-overview)
- [Additional Notes](#additional-notes)
- [Author](#author)


## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security (JWT)
- MySQL
- Redis (via Docker)
- Maven
- JPA / Hibernate

## Project Structure

```
src/
├── common/               # Common utilities, constants, and shared helpers
├── config/               # Global configurations (e.g., WebMvc, Security)
├── controller/           # REST controllers
├── dto/                  # Data Transfer Objects
├── exception/            # Custom exceptions and global exception handlers
├── mapper/               # Entity-DTO mappers
├── model/                # JPA entities
├── repository/           # Spring Data JPA repositories
├── security/             # JWT filters, authentication handlers, etc.
├── service/              # Business logic
└── validator/            # Custom validation logic
```

## How to Run

### Prerequisites

- Java 17
- Maven
- MySQL running locally
- Docker (for Redis)

### Start Redis via Docker

```bash
docker run --name forum-redis -p 6379:6379 -d redis
```

### Configure Application

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/forum
spring.datasource.username=yourUsername
spring.datasource.password=yourPassword
spring.redis.host=localhost
spring.redis.port=6379
jwt.secret=yourSecretKey
```

### Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

## Features

- JWT-based Authentication
- CRUD operations for Posts & Comments
- Nested Comments (Replies)
- Like/Dislike for Comments and Posts
- Community Join/Leave and Membership Validation
- Follow System
- Real-Time Notification System (in-DB + future Email Support)
- Redis Caching for Recent Views and Post History (for unauthenticated users)

## API Docs
Swagger UI: http://localhost:8080/swagger-ui/index.html

## API Endpoints Overview

### Auth
- `POST /auth/signup` - User registration
- `POST /auth/login` - User login
- `GET /auth/me` - Get current logged-in user

### User / Profile
- `GET /profile/{username}` - View user profile
- `POST /profile/{username}/nickname` - Update nickname
- `POST /profile/{username}/bio` - Update bio
- `POST /profile/{username}/image` - Update profile image

### Post
- `POST /posts` - Create a post
- `PUT /posts/{id}` - Edit a post
- `DELETE /posts/{id}` - Delete a post
- `GET /posts/{id}` - Get a post
- `GET /posts/recent` - Get recent viewed posts (by token or localStorage)
- `GET /posts/top` - Get top liked posts
- `GET /posts/search` - Search posts

### Comment
- `POST /comments` - Create a comment
- `POST /comments/reply` - Create a reply
- `DELETE /comments/{id}` - Delete a comment
- `GET /comments/post/{postId}` - Get all comments for a post

### Like
- `POST /posts/{id}/like` - Toggle like on a post
- `POST /comments/{id}/like` - Toggle like on a comment
- `POST /comments/{id}/dislike` - Toggle dislike on a comment

### Notification
- `GET /notifications` - Get all notifications for user
- `POST /notifications/mark-all-read` - Mark all as read
- `GET /notifications/{id}/link` - Resolve and navigate to the target

### Community
- `POST /communities` - Create a new community
- `GET /communities/{id}` - View a community
- `POST /communities/{id}/join` - Join a community
- `GET /communities/me` - Get joined communities

### Follow
- `POST /follow/{username}` - Toggle follow
- `GET /follow/{username}` - Get follow status

## Additional Notes

- Comment and Post likes trigger notifications.
- Replies and new comments notify the author.
- Recent post view tracking for guests is backed by Redis.
- Clean separation of validation, mapping, and business logic.
- Modular design for easy testing and future scaling.

## Author

Donghyeon Lee - Forum Backend Developer (Spring Boot)

---

This backend is part of a full-stack forum application with a React frontend.

# Forum Backend (Spring Boot)

This is the backend of a **full-stack forum application** built with Java Spring Boot.  
It powers **authentication, posts, comments, reactions, bookmarks, communities, follows, reports, chat, trending, and notifications**.  
Redis via Docker is used for caching recent views and guest post history.  
AWS S3 is integrated for file and media storage.  
The backend is designed to scale with clean modular architecture and integrates with a React frontend.

---

## Table of Contents
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [Features](#features)
- [API Endpoints Overview](#api-endpoints-overview)
- [Additional Notes](#additional-notes)
- [Author](#author)

---

## Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **Spring Security (JWT)**
- **MySQL**
- **Redis** (via Docker)
- **Gradle**
- **Hibernate**
- **AWS S3** (media storage)
- **WebSocket (STOMP)** for chat

---

## Project Structure

```
src/main/java/com/example/forum
├── common/                 # Shared helpers, constants, utilities
├── config/                 # Global configs (Redis, S3, Security, Web, WebSocket)
├── controller/
│   ├── auth/
│   ├── bookmark/           # Bookmark APIs & Swagger docs
│   ├── chat/               # Chat APIs, docs, websocket endpoints
│   ├── comment/            # Comment APIs & docs
│   ├── community/          # Community APIs (create, join, leave, manage)
│   ├── follow/             # Follow system APIs
│   ├── notification/       # Notification APIs
│   ├── post/               # Post CRUD, media, reaction, hide, trending
│   ├── profile/            # Profile update/view APIs
│   ├── report/             # Report system APIs (post, comment, user, community)
│   ├── search/             # Search APIs
│   ├── tag/                # Tag APIs
│   └── trending/           # Trending APIs
├── dto/                    # DTOs
├── exception/              # Global exception handling
├── factory/                # Test/data factory utils
├── helper/                 # Helpers (e.g., pagination, formatting)
├── mapper/                 # Entity <-> DTO mappers
├── model/                  # Entities
│   ├── bookmark/
│   ├── chat/
│   ├── comment/
│   ├── community/
│   ├── follow/
│   ├── like/
│   ├── notification/
│   ├── post/               # Post, HiddenPost, PostFile, PostTag, enums
│   ├── profile/
│   ├── report/
│   ├── tag/
│   └── user/
├── repository/             # Spring Data JPA repositories
├── security/               # JWT filters, user details, handshake interceptors
├── service/                # Business logic
│   ├── auth/
│   ├── bookmark/
│   ├── chat/
│   ├── comment/
│   ├── community/          # CommunityService, Manage, Member, Post
│   ├── follow/
│   ├── notification/
│   ├── post/               # Post CRUD, Media, Trending, Hidden, View
│   ├── profile/
│   ├── reaction/           # Reaction for posts & comments
│   ├── report/
│   ├── search/
│   ├── tag/
│   └── util/
├── validator/              # Custom validators for each domain
└── BackendApplication.java # Spring Boot entry point
```

---

## How to Run

### Prerequisites

- Java 17
- Gradle
- MySQL running locally
- Docker (for Redis)
- AWS S3 bucket (for media uploads)

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

aws.accessKey=yourAccessKey
aws.secretKey=yourSecretKey
aws.s3.bucket=yourBucket
aws.s3.region=us-east-2
```

### Build and Run

```bash
./gradlew clean build
./gradlew bootRun
```

---

## Features

- **Authentication**
  - JWT-based login/signup
  - User profile management
- **Posts**
  - CRUD operations
  - Post hide/unhide
  - File & media uploads (S3)
  - Tagging system
  - Trending posts
  - Bookmark posts
- **Comments**
  - Nested replies
  - Like/Dislike system
- **Communities**
  - Create, join, leave communities
  - Manage roles & permissions
  - Favorite communities
- **Follow System**
  - Follow/unfollow users
  - Followers/following lists
- **Bookmarks**
  - Save/unsave posts
- **Reports**
  - Report users, posts, comments, communities
  - Moderation actions
- **Chat**
  - Real-time private and community chat (WebSocket)
- **Notifications**
  - Real-time notifications for likes, comments, replies, follows, reports
  - Mark all as read
- **Redis**
  - Guest post history
  - Recently viewed posts

---

## API Docs

Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## API Endpoints Overview

### Auth
- `POST /auth/signup` - User registration
- `POST /auth/login` - User login
- `GET /auth/me` - Get current logged-in user

### Profile
- `GET /profile/{username}` - View user profile
- `POST /profile/{username}/nickname` - Update nickname
- `POST /profile/{username}/bio` - Update bio
- `POST /profile/{username}/image` - Update profile image

### Post
- `POST /posts` - Create a post
- `PUT /posts/{id}` - Edit a post
- `DELETE /posts/{id}` - Delete a post
- `GET /posts/{id}` - Get a post
- `GET /posts/recent` - Get recent viewed posts
- `GET /posts/top` - Get top liked posts
- `GET /posts/search` - Search posts
- `POST /posts/{id}/bookmark` - Toggle bookmark
- `POST /posts/{id}/hide` - Hide a post

### Comment
- `POST /comments` - Create a comment
- `POST /comments/reply` - Create a reply
- `DELETE /comments/{id}` - Delete a comment
- `GET /comments/post/{postId}` - Get all comments for a post

### Reaction
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
- `POST /communities/{id}/leave` - Leave a community
- `GET /communities/me` - Get joined communities
- `POST /communities/{id}/favorite` - Toggle favorite

### Follow
- `POST /follow/{username}` - Toggle follow
- `GET /follow/{username}` - Get follow status

### Report
- `POST /reports` - Create a report (post, comment, user, community)
- `GET /reports` - Get all reports (admin)
- `POST /reports/{id}/action` - Take moderation action

### Chat
- WebSocket endpoint for real-time chat between users
- Supports private messaging and community chat channels

---

## Additional Notes

- Likes, comments, replies, follows, and reports trigger notifications.
- Redis backs guest post history and recent views.
- AWS S3 integration for scalable media storage.
- Clean separation of validation, mapping, and business logic.
- Modular design for easy testing and future scaling.

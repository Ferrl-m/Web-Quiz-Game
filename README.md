# Web Quiz Game API
This is the backend API for the Web Quiz Game project, built using Spring Boot, Spring Security, and Hibernate. The API provides a RESTful interface for managing quizzes and quiz submissions.

## Requirements
To build and run the Web Quiz Game API, you will need the following installed on your system:

- Java 11
- Gradle 6.8 or higher
- PostgreSQL 12 or higher

## Setup

1. Clone the repository
2. Create a PostgreSQL database and update the `application.properties` file with your database details
3. Build and run the project

## API Endpoints

The following API endpoints are available:

- `GET /api/quizzes` - Returns a list of all quizzes
- `GET /api/quizzes/{id}` - Returns the quiz with the specified ID
- `POST /api/quizzes` - Creates a new quiz
- `POST /api/quizzes/{id}/solve` - Submits an answer for the quiz with the specified ID
- `DELETE /api/quizzes/{id}` - Deletes the quiz with the specified ID
- `GET /api/quizzes/completed` - Returns a list of completed quizzes of current user
- `POST /api/register` - Register a new user

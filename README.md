# Web Quiz Game
This is the Spring web app for the Quiz Game project, built using Spring Boot, Spring Security, and Hibernate.

## Requirements
To build and run the Web Quiz Game, you will need the following installed on your system:

- Java 17
- Gradle 6.8 or higher
- PostgreSQL 12 or higher

## Setup

1. Clone the repository
2. Create a PostgreSQL database and update the `application.properties` file with your database details
3. Build and run the project

## Endpoints

The following endpoints are available:
### GET
- `GET /quizzes/{page}` - Open a list of quizzes
- `GET /register` - Open registration page
- `GET /login` - Open login page
- `GET /login` - Perform logout
- `GET /quiz/{id}` - Open the quiz with the specified ID
- `GET /quizzes/create` - Open page for creating new quiz
- `GET /quizzes/{id}/solve` - Open solving page for quiz with specified ID
- `GET /quizzes/solve` - Open random quiz
- `GET /quizzes/user/{page}/{username}` - Open a list of quizzes of specific user
- `GET /profile/{username}` - Open profile of specific user
- `GET /quizzes/{theme}/{page}` - Open a list of quizzes by specific theme

### POST
- `POST /quizzes/{id}/solve` - Submits an answer for the quiz with the specified ID
- `POST /quizzes/create` - Create quiz
- `POST /register` - Submit user registration
- `POST /login` - Submit user login
- `POST /rate{id}` - Rate specific quiz
- `POST /users/change-credentials` - Update user credentials

### DELETE
- `DELETE /quizzes/{id}` - Deletes the quiz with the specified ID
- `DELETE /users/delete/{username}` - Deletes account


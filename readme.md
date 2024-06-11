# Yoga App

## Introduction

Yoga App is an application designed to assist local small businesses in managing yoga sessions. Developed by NumDev agency, this full-stack solution provides an intuitive interface for both administrators and end-users to manage yoga sessions effectively.

## Prerequisites

Before you begin, ensure you have installed the following:
- Java 11
- NodeJS 16
- MySQL
- Angular CLI 14

## Installation
1. Create your Database
**MySQL Database:**

Open terminal & run the following command :
```shell
mysql -u root -p
```
Then, enter your mysql admin password. 

Create a new database for your application and add all the tables to your database:

```sql
DROP DATABASE IF EXISTS `test`;

CREATE DATABASE `test`;

USE `test`;

CREATE TABLE `TEACHERS` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `last_name` VARCHAR(40),
  `first_name` VARCHAR(40),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `SESSIONS` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(50),
  `description` VARCHAR(2000),
  `date` TIMESTAMP,
  `teacher_id` int,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `USERS` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `last_name` VARCHAR(40),
  `first_name` VARCHAR(40),
  `admin` BOOLEAN NOT NULL DEFAULT false,
  `email` VARCHAR(255),
  `password` VARCHAR(255),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `PARTICIPATE` (
  `user_id` INT, 
  `session_id` INT
);

ALTER TABLE `SESSIONS` ADD FOREIGN KEY (`teacher_id`) REFERENCES `TEACHERS` (`id`);
ALTER TABLE `PARTICIPATE` ADD FOREIGN KEY (`user_id`) REFERENCES `USERS` (`id`);
ALTER TABLE `PARTICIPATE` ADD FOREIGN KEY (`session_id`) REFERENCES `SESSIONS` (`id`);

INSERT INTO TEACHERS (first_name, last_name)
VALUES ('Margot', 'DELAHAYE'),
       ('Hélène', 'THIERCELIN');

INSERT INTO USERS (first_name, last_name, admin, email, password)
VALUES ('Admin', 'Admin', true, 'yoga@studio.com', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq'),
```

2. Clone the project from the GitHub repository.

3. Navigate to the project directory.

### Backend Setup
- Open a terminal and navigate to the backend folder.
- Run the command `mvn clean install` to install all Maven dependencies.
- Configure the application in the `application.properties` file located in `src/main/resources/` with your database environment. 

### Frontend Setup
- Open a new terminal and navigate to the frontend folder.
- Run `npm install` to install all npm dependencies.

## Initializing the Project

### Database Setup
- Ensure MySQL is running on the default port 3306.
- Initialize the database using the provided schema and data scripts.

### Starting the Servers
- Start the backend server by running `mvn spring-boot:run` from the backend directory.
- Start the frontend by navigating to the frontend directory and running `nnpm run start`.

## Usage

### Admin Account
- As an admin, you can add, modify, and delete yoga sessions.

### User Account
- Create a new user account or log in using existing user credentials.
- As a user, you can participate in yoga sessions.

## Testing

### Frontend
- For Unit & Integrations tests, run the following command : 
Stack Used: Jest.
```shell
npm run test
```

This will lauch all tests & generate a report in `/coverage/jest/index.html`

- For e2e tests, run the following command : 
Stack Used: Cypress.

Lauch tests on web interface :
```shell
npm run e2e:ci
```

Lauch tests on IHM :
```shell
npm run e2e
```

Lauch e2e coverage :
```shell
npm run e2e:coverage
```

This will lauch all tests & generate a report in `/coverage/Icov-report/index.html`

### Backend 

To launch all tests & get the coverage file, launch the following command : 

```shell
mvn clean site
```

The coverage is located in `/target/site/jacoco/index.html`

## Points of Caution

- Ensure the database is running on MySQL's default port 3306.
- Start the API server before using the frontend to avoid connection issues.

## Contributing

Please read CONTRIBUTING.md for details on our code of conduct, and the process for submitting pull requests to us.

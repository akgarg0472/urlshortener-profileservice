# URL Shortener Profile Service

![Java Version](https://img.shields.io/badge/Java-21-blue)
![version](https://img.shields.io/badge/version-1.0.2-blue)

## Table of Contents

- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
    - [application.yml](#applicationyml)
    - [application-prod.yml](#application-prodyml)
- [Docker Setup](#docker-setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Additional Notes](#additional-notes)

## Introduction

The **URL Shortener Profile Service** is designed to shorten long URLs and store associated profile data for users. This
service enables users to manage their profiles while also shortening URLs for quick sharing. It is built using **Spring
Boot** and **JPA** for database interaction, and the application supports **MySQL** as the database. It is also
Dockerized to make the deployment process smooth and scalable.

## Prerequisites

Before running the project, ensure you have the following installed:

- **Java 21+** (JDK)
- **Maven**
- **Docker**

## Installation

### Clone the Repository

Clone the repository to your local machine:

 ```bash
 git clone https://github.com/akgarg0472/urlshortener-profileservice
 cd urlshortener-profileservice
 ```

### Build the Application

To build the application locally, use Maven to compile the project and generate the JAR file:

```bash
./mvnw clean package -DskipTests
```

This will compile the application and generate the executable `.jar` file in the `target` directory.

## Configuration

The application uses two main configuration files: `application.yml` for general Spring Boot settings and
`application-prod.yml` for production-specific configurations such as database, Kafka and JPA settings.

### application.yml

This file contains general configurations related to the Spring Boot application, Eureka, and server settings.

```yml
spring:
  application:
    name: urlshortener-profile-service
  kafka:
    bootstrap-servers: localhost:9092
  profiles:
    active: prod

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    enabled: true

server:
  port: 8566
```

#### Key Sections:

- **spring.application.name**: The name of the Spring Boot application.
- **spring.kafka.bootstrap-servers**: Kafka server settings for communication.
- **spring.profiles.active**: Specifies the active profile, here set to prod.
- **Eureka**: Configuration for the Eureka service registry.
- **Server Port**: Configures the port for the server (8566 in this case).

### application-prod.yml

This file contains production-specific configurations, including the database connection, JPA, Kafka and Hikari
connection pool settings.

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/urlshortener?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      pool-name: ProfileServiceConnectionPool
      maximum-pool-size: 20
      minimum-idle: 5
      max-lifetime: 60000
      connection-timeout: 30000
      idle-timeout: 600000
      leak-detection-threshold: 60000
      connection-test-query: SELECT 1
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        show_sql: false
        format_sql: false
    open-in-view: false

kafka:
  notification:
    topic:
      name: urlshortener.notifications.email
      partitions: 1
      replication-factor: 1
```

#### Key Sections:

- **Datasource Configuration**: Database connection settings (MySQL).
- **JPA Settings**: Configures Hibernate and JPA behavior (e.g., ddl-auto: update).
- **Hikari Connection Pool**: Connection pool settings for optimal database performance.
- **Kafka Notification Topic**: Defines Kafka topic settings for email notifications.

## Logging Configuration

The URL Shortener Service uses environment variables for logging configuration. Here are the available environment
variables that you can customize for logging:

- **LOG_LEVEL**: Specifies the log level for the application (e.g., `INFO`, `DEBUG`, `WARN`).
    - Default value: `INFO`

- **LOG_PATH**: Specifies the base path for log files.
    - Default value: `/tmp/logs/profile`

- **LOGGER_REF**: Specifies the appender reference to use for logging.
    - Default value: `consoleLogger`
    - Allowed values: `consoleLogger`, `fileLogger`

### Environment Variables

To customize the configuration, the application can use the following environment variables. These can be passed at
runtime, especially useful when running in Docker containers.

- **SPRING_PROFILES_ACTIVE**: Set the active Spring profile (e.g., prod).
- **SPRING_DATASOURCE_URL**: The JDBC URL for the MySQL database.
- **SPRING_DATASOURCE_USERNAME**: The username for the database connection.
- **SPRING_DATASOURCE_PASSWORD**: The password for the database connection.
- **SPRING_DATASOURCE_DRIVER_CLASS_NAME**: The JDBC driver class name.
- **SPRING_JPA_HIBERNATE_DDL_AUTO**: The Hibernate DDL mode (e.g., update).
- **SPRING_JPA_PROPERTIES_HIBERNATE_SHOW_SQL**: Whether Hibernate should log SQL queries.
- **SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL**: Whether Hibernate should format SQL queries.
- **SPRING_JPA_OPEN_IN_VIEW**: Whether Spring should leave a Hibernate session open for views.
- **SPRING_KAFKA_NOTIFICATION_TOPIC_NAME**: Kafka topic name for notifications.
- **CLOUDINARY_URL**: **Required** in the `prod` profile. This URL is necessary to integrate with Cloudinary for media
  storage (such as profile images or other file uploads). It should be in the following format:

  ```text
  CLOUDINARY_URL=cloudinary://<your_api_key>:<your_api_secret>@cloud_name
  ```

  You can obtain this URL from your Cloudinary account.

> Note: If you're using the `prod` profile, make sure to define the `CLOUDINARY_URL` environment variable, or the
> application will fail to start.

## Docker Setup

The project is Dockerized for easy deployment. The `Dockerfile` is already configured to build and run the Spring Boot
application.

The `Dockerfile` defines the build and runtime configuration for the container.

### Build the Docker Image

To build the Docker image, run the following command:

```bash
docker build -t akgarg0472/urlshortener-profile-service:tag .
```

### Run the Docker Container

You can run the application with custom environment variables using the docker run command. For example:

```bash
docker run -e SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/database_name?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
           -e SPRING_DATASOURCE_USERNAME="root" \
           -e SPRING_DATASOURCE_PASSWORD="root" \
           -e SPRING_PROFILES_ACTIVE="prod" \
           -e SPRING_KAFKA_NOTIFICATION_TOPIC_NAME="urlshortener.notifications.email" \
           -e CLOUDINARY_URL="CLOUDINARY_URL=cloudinary://<your_api_key>:<your_api_secret>@cloud_name" \ 
           --network=host \
           akgarg0472/urlshortener-profile-service:tag
```

This will start the container with the necessary environment variables.

## Running the Application

If you prefer to run the application locally without Docker, use the following Maven command:

```bash
./mvnw spring-boot:run  
```

This will start the application on the default port `8566`. If you'd like to customize the port, you can set the
`server.port` property in `application.yml` or override it via environment variables.

## API Documentation

The **API Documentation** for the URL Shortener Profile Service is automatically generated using **Springdoc OpenAPI**
and can be accessed at the following endpoints:

1. **OpenAPI Specification**: Available at:

    ```text
    http://<host>:<port>/api-docs
    ```

   This provides the raw OpenAPI specification in JSON format, which can be used for integrations or importing into API
   tools.

2. **Swagger UI**: The user-friendly API documentation is accessible at:

    ```text
    http://<host>:<port>/docs
    ```

Replace `<host>` and `<port>` with your application's host and port. For example, if running locally:

- OpenAPI Specification: [http://localhost:8566/api-docs](http://localhost:8566/api-docs)
- Swagger UI: [http://localhost:8566/docs](http://localhost:8566/docs)

The Swagger UI provides detailed information about the available endpoints, including request and response formats,
sample payloads, and error codes, making it easy for developers to integrate with the service.

## Additional Notes

- *MySQL Connectivity Issues*: Ensure your MySQL service is up and running, and that the database URL, username, and
  password are correctly set. If you’re using Dockerized MySQL, make sure both the database container and the app
  container are on the same network.
- *Profiles*: The active Spring profile (SPRING_PROFILES_ACTIVE) can be overridden via the -e flag during Docker
  container runtime or through environment variables locally. Make sure to use prod profile unless unit testing.
- *Port Conflicts*: If port 8566 is already in use, you can override it by setting server.port in the environment
  or in the application.yml.
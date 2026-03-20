# Application Startup & Setup Guide

This document explains how this project was initially generated and how to run it.

## 1. Initial Scaffolding Command
This project was generated using Spring Initializr to configure the base application, install the necessary dependencies in `pom.xml`, and set up the root package structure. The following command was used:

```bash
curl -sSL https://start.spring.io/starter.zip \
  -d dependencies=web,data-jpa,postgresql,validation,lombok \
  -d type=maven-project \
  -d groupId=com.ecommerce \
  -d artifactId=ecommerce \
  -d name=ecommerce \
  -d packageName=com.ecommerce \
  -d javaVersion=17 \
  -o starter.zip && unzip -o starter.zip && rm starter.zip
```

### Dependencies Explained
* `web`: Embedded Tomcat web server for building REST APIs.
* `data-jpa`: Object-Relational Mapping (ORM) used to map Java objects to SQL automatically.
* `postgresql`: The PostgreSQL database driver.
* `validation`: Checks incoming HTTP requests for valid payloads.
* `lombok`: Auto-generates getters, setters, toString methods, and constructors to reduce Java boilerplate.

---

## 2. Generating the Clean Architecture Modular Structure
After generation, the source code was organized by "Feature Modules" (`auth` and `product`). The following command was used in the `src/main/java/com/ecommerce` directory to scaffold the standard Clean Architecture layers:

```bash
mkdir -p auth/domain/entity auth/domain/repository auth/domain/exception
mkdir -p auth/application/usecase auth/application/service
mkdir -p auth/infrastructure/configuration auth/infrastructure/persistence/entity auth/infrastructure/persistence/adapter
mkdir -p auth/presentation/controller auth/presentation/dto auth/presentation/mapper

# Repeated for the 'product' module
mkdir -p product/domain/entity product/domain/repository product/domain/exception
mkdir -p product/application/usecase product/application/service
mkdir -p product/infrastructure/configuration product/infrastructure/persistence/entity product/infrastructure/persistence/adapter
mkdir -p product/presentation/controller product/presentation/dto product/presentation/mapper
```

---

## 3. How to Run the Application

You can start the server entirely from your terminal using the built-in Maven Wrapper (no global Maven installation required):

**On Mac/Linux:**
```bash
./mvnw spring-boot:run
```

**On Windows:**
```cmd
mvnw.cmd spring-boot:run
```

When started, Tomcat will run by default on **http://localhost:8080**.

**Note:** By default, the application expects a valid PostgreSQL database connection defined in your `src/main/resources/application.properties`. If you need to test the application before configuring your database, you can temporarily disable the active Database connection requirement by adding:
```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

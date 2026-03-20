# Project Folder Structure Guide

This project structure favors a **"Package by Feature"** (Modular Monolith) layout instead of a traditional "Package by Layer" layout. This means features like `auth` and `product` have their own isolated folder structures inside `src/main/java`.

## Root Level Directory
* **`pom.xml`**: The Maven manifest file. Manages external libraries, Java versions, and build scripts.
* **`mvnw` / `mvnw.cmd`**: Maven Wrapper scripts. Allows any developer to build the app without needing to install Maven globally on their machine.
* **`Doc/`**: Project documentation files.

## The `src/main/` Directory 
Code and configurations for the application.

### `src/main/resources/` (Configuration & Assets)
* **`application.properties`**: Your central configuration hub. Define the Database URL, server port, environment variables, and external API keys here.
* **`static/`**: For standard web files (HTML/CSS/JS). We leave this empty because we are building a REST API.
* **`templates/`**: For server-side rendering (e.g., Thymeleaf templates). We also leave this empty.

### `src/main/java/` (The Java Code)
Root package: `com.ecommerce`

* **`EcommerceApplication.java`**: The main entry point. The `@SpringBootApplication` annotation boots the web server and scans the folders below it for components to load into the Spring ApplicationContext.

#### Feature Modules (`auth/` & `product/`)
Inside each feature module, we strictly follow 4 Clean Architecture layers:

1. **`domain/`**: (Inner Core)
   * Pure Java code. No database annotations (`@Entity`) or web annotations (`@RestController`) are allowed here.
   * `entity/`: The Core business objects holding state and validation rules (e.g., `Product.java`).
   * `repository/`: Interface definitions requiring data persistence methods like `save()` and `findById()`.
   * `exception/`: Business-specific errors (e.g., `InvalidPriceException.java`).

2. **`application/`**: (Use Cases)
   * `usecase/`: The Orchestrators (e.g., `CreateProductUseCase.java`). They take input from the Presentation layer, enforce Domain rules, and save data via the Infrastructure layer.

3. **`infrastructure/`**: (External Integrations & DB)
   * `persistence/entity/`: Database Schemas mapping directly to tables using JPA `@Entity`.
   * `persistence/adapter/`: Implements the abstract `domain/repository/` interfaces, executing the actual saving and fetching logic via Spring Data JPA.
   * `configuration/`: Specific Spring `@Bean` wires or external web clients specific to this module.

4. **`presentation/`**: (Web / APIs)
   * `controller/`: REST Web APIs mapping HTTP requests to UseCases (e.g., `ProductController.java`).
   * `dto/`: Request and Response Data Transfer Objects (Payloads).
   * `mapper/`: Rules for translating incoming payloads into Domain Entities (e.g., `ProductDTO -> Product`).

---
## The `src/test/` Directory
Where your automated unit, integration, and end-to-end tests live. The package structure here mimics the identical nested layout found in `src/main/java/`.

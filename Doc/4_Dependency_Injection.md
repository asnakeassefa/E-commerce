# Dependency Injection in Spring Boot

Dependency Injection (DI) is a design pattern where objects receive their dependencies from the outside, rather than creating them internally. Spring Boot has DI **built-in** — no extra libraries needed.

---

## 🧩 Why Dependency Injection?

Without DI, classes create their own dependencies:

```java
// ❌ Bad — tightly coupled
public class CreateProductUseCase {
    private final ProductRepositoryAdapter repo = new ProductRepositoryAdapter();
}
```

This breaks Clean Architecture because the **UseCase** (inner layer) now directly depends on the **Adapter** (outer layer). You can't swap implementations or write unit tests easily.

With DI, classes declare **what they need**, and Spring provides it:

```java
// ✅ Good — loosely coupled
public class CreateProductUseCase {
    private final ProductRepository repo;  // interface, not concrete class

    public CreateProductUseCase(ProductRepository repo) {
        this.repo = repo;  // Spring injects the right implementation
    }
}
```

---

## 🏷️ Spring Annotations for DI

Spring uses annotations to discover and wire classes together automatically.

### Registration Annotations

These tell Spring: *"This class is a dependency — register it in the container."*

| Annotation | Purpose | Where to Use |
|---|---|---|
| `@Component` | Generic Spring-managed class | Infrastructure adapters, utilities |
| `@Service` | Semantic alias for `@Component` | Application layer use cases & services |
| `@Repository` | Alias for `@Component` + DB exception translation | Infrastructure persistence adapters |
| `@RestController` | Alias for `@Component` + REST endpoint handling | Presentation layer controllers |
| `@Configuration` | Declares a class that produces `@Bean` definitions | Module-specific config classes |

> All of these are specializations of `@Component`. Spring treats them the same way internally — the different names exist for **readability** and **semantic clarity**.

### Injection: How Dependencies Are Provided

Spring supports three injection styles. **Constructor injection is the recommended approach.**

#### ✅ Constructor Injection (Recommended)

```java
@Service
public class CreateProductUseCase {

    private final ProductRepository productRepository;

    // Spring sees this constructor, finds a @Component that implements
    // ProductRepository, and passes it in automatically.
    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}
```

**Why this is best:**
- Fields can be `final` (immutable after construction)
- Easy to unit test — just pass a mock in the constructor
- Fails fast at startup if a dependency is missing

#### ⚠️ Field Injection (Not Recommended)

```java
@Service
public class CreateProductUseCase {

    @Autowired
    private ProductRepository productRepository;  // Spring injects directly into the field
}
```

**Why to avoid:** Fields can't be `final`, harder to test, hides dependencies.

---

## 🔄 How It Connects in Clean Architecture

### 1. Domain Layer — Define the Interface (Port)

```java
// domain/repository/ProductRepository.java
public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
}
```

No annotations. Pure Java. The domain doesn't know Spring exists.

### 2. Infrastructure Layer — Implement the Interface (Adapter)

```java
// infrastructure/persistence/adapter/ProductRepositoryAdapter.java
@Component
public class ProductRepositoryAdapter implements ProductRepository {

    @Override
    public Product save(Product product) {
        // Actual JPA database logic here
        return product;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        // Actual JPA query here
        return Optional.empty();
    }
}
```

`@Component` registers this class. Spring knows it implements `ProductRepository`.

### 3. Application Layer — Consume via Constructor

```java
// application/usecase/CreateProductUseCase.java
@Service
public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(Product product) {
        product.validateCoreFields();
        return productRepository.save(product);
    }
}
```

Spring automatically injects `ProductRepositoryAdapter` here because it's the only `@Component` implementing `ProductRepository`.

### 4. Presentation Layer — Consume the UseCase

```java
// presentation/controller/ProductController.java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase) {
        this.createProductUseCase = createProductUseCase;
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductRequest request) {
        Product product = mapToDomain(request);
        Product saved = createProductUseCase.execute(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
```

---

## 🚀 What Happens at Startup

When `EcommerceApplication.java` runs, Spring Boot:

```
1. Scans all packages under com.ecommerce
2. Finds every class annotated with @Component, @Service, @Repository, @RestController
3. Creates a single instance (singleton) of each
4. Looks at each constructor to see what dependencies are needed
5. Wires them together automatically:

   ProductRepositoryAdapter (implements ProductRepository)
       ↓ injected into
   CreateProductUseCase (needs ProductRepository)
       ↓ injected into
   ProductController (needs CreateProductUseCase)
```

If any dependency is missing (e.g., no class implements `ProductRepository`), the application **fails to start** with a clear error message — so you catch wiring problems immediately.

---

## 📌 Key Rules

1. **Always use constructor injection** — avoid `@Autowired` on fields.
2. **Depend on interfaces, not concrete classes** — this is what makes layers swappable.
3. **One implementation per interface** in most cases. If you have multiple, use `@Primary` or `@Qualifier` to tell Spring which one to inject.
4. **Domain layer has zero annotations** — no `@Component`, no `@Autowired`. It stays pure.

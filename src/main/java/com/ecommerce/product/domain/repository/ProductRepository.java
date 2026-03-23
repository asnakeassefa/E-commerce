package com.ecommerce.product.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ecommerce.product.domain.entity.Product;

public interface ProductRepository {
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    Product save(Product product);
    Product update(Product product, UUID id);
    void delete(UUID id);
}

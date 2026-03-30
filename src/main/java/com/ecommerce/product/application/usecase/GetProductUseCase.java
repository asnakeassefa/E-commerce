package com.ecommerce.product.application.usecase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.domain.exception.ProductNotFoundException;
import com.ecommerce.product.domain.repository.ProductRepository;

public class GetProductUseCase {
    private final ProductRepository productRepository;

    public GetProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(UUID id) {
        final Optional<Product> product = productRepository.findById(id);
        return product.orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    public List<Product> executeAll() {
        return productRepository.findAll();
    }

}

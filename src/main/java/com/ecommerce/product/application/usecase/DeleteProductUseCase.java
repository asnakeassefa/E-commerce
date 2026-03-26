package com.ecommerce.product.application.usecase;

import java.util.UUID;

import com.ecommerce.product.domain.repository.ProductRepository;

public class DeleteProductUseCase {
    ProductRepository productRepository;

    public DeleteProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void execute(UUID id) {
        productRepository.delete(id);
    }
}

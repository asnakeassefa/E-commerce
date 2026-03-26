package com.ecommerce.product.application.usecase;

import java.util.UUID;

import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.domain.exception.ProductNotFoundException;
import com.ecommerce.product.domain.repository.ProductRepository;

public class UpdateProductUseCase {
    ProductRepository productRepository;

    public UpdateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(Product product, UUID id) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        existingProduct.update(product);
        existingProduct.validateCoreFields();
        existingProduct.markUpdated();
        Product response = productRepository.save(existingProduct);
        return response;
    }
}

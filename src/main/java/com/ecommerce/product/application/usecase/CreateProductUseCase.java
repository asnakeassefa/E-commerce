package com.ecommerce.product.application.usecase;

import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.domain.repository.ProductRepository;

public class CreateProductUseCase {
    ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(Product product) {
        product.validateCoreFields();
        product.markCreate();
        Product response = productRepository.save(product);
        return response;
    }
}

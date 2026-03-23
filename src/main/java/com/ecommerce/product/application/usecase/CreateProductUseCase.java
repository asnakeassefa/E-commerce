package com.ecommerce.product.application.usecase;

import org.springframework.stereotype.Service;

import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.domain.repository.ProductRepository;

@Service
public class CreateProductUseCase {
    ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(Product product) {
        Product response = productRepository.save(product);
        return response;
    }
}

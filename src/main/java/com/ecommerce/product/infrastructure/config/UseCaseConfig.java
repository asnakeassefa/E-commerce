package com.ecommerce.product.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecommerce.product.application.usecase.CreateProductUseCase;
import com.ecommerce.product.application.usecase.DeleteProductUseCase;
import com.ecommerce.product.application.usecase.GetProductUseCase;
import com.ecommerce.product.application.usecase.UpdateProductUseCase;
import com.ecommerce.product.domain.repository.ProductRepository;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepository productRepository) {
        return new CreateProductUseCase(productRepository);
    }

    @Bean
    public GetProductUseCase getProductUseCase(ProductRepository productRepository) {
        return new GetProductUseCase(productRepository);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepository productRepository) {
        return new UpdateProductUseCase(productRepository);
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductRepository productRepository) {
        return new DeleteProductUseCase(productRepository);
    }
}

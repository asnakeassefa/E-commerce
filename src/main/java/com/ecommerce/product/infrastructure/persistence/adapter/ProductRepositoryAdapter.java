package com.ecommerce.product.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.domain.repository.ProductRepository;
import com.ecommerce.product.infrastructure.persistence.entity.ProductJpaEntity;
import com.ecommerce.product.infrastructure.persistence.repository.JpaProductRepository;

@Component
public class ProductRepositoryAdapter implements ProductRepository {
    private final JpaProductRepository jpaProductRepository;

    public ProductRepositoryAdapter(JpaProductRepository jpaProductRepository) {
        this.jpaProductRepository = jpaProductRepository;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll().stream()
                .map(ProductJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Product save(Product product) {
        product.validateCoreFields();
        ProductJpaEntity productJpaEntity = ProductJpaEntity.fromDomain(product);
        ProductJpaEntity savedProductJpaEntity = jpaProductRepository.save(productJpaEntity);
        return ProductJpaEntity.toDomain(savedProductJpaEntity);
    }

    @Override
    public Product update(Product product, UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}

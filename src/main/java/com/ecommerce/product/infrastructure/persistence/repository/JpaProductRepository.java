package com.ecommerce.product.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.product.infrastructure.persistence.entity.ProductJpaEntity;

public interface JpaProductRepository extends JpaRepository<ProductJpaEntity, UUID> {
}

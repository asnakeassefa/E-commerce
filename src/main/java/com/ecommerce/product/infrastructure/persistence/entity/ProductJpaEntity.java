package com.ecommerce.product.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ecommerce.product.domain.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
// database table name
@Table(name = "products")
public class ProductJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(name = "original_price", nullable = false)
    private BigDecimal originalPrice;

    @Column(name = "selling_price", nullable = false)
    private BigDecimal sellingPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ProductJpaEntity() {
    }

    // Domain Entity to Jpa Entity

    public static ProductJpaEntity fromDomain(Product product) {
        ProductJpaEntity productJpaEntity = new ProductJpaEntity();
        productJpaEntity.id = product.getId();
        productJpaEntity.name = product.getName();
        productJpaEntity.description = product.getDescription();
        productJpaEntity.sku = product.getSku();
        productJpaEntity.originalPrice = product.getOriginalPrice();
        productJpaEntity.sellingPrice = product.getSellingPrice();
        productJpaEntity.createdAt = product.getCreatedAt();
        productJpaEntity.updatedAt = product.getUpdatedAt();
        return productJpaEntity;
    }

    public static Product toDomain(ProductJpaEntity productJpaEntity) {
        return Product.builder()
                .id(productJpaEntity.id)
                .name(productJpaEntity.name)
                .description(productJpaEntity.description)
                .sku(productJpaEntity.sku)
                .originalPrice(productJpaEntity.originalPrice)
                .sellingPrice(productJpaEntity.sellingPrice)
                .createdAt(productJpaEntity.createdAt)
                .updatedAt(productJpaEntity.updatedAt)
                .build();
    }

    public void setId(UUID randomUUID) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setId'");
    }

}

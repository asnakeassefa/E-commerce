package com.ecommerce.product.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ecommerce.product.domain.exception.InvalidProductException;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder(builderClassName = "ProductBuilder")
public class Product {
    private UUID id;
    // setting only setters for the fields that can be updated
    @Setter
    private String name;
    @Setter
    private String description;
    @Setter
    private String sku;
    @Setter
    private BigDecimal originalPrice;
    @Setter
    private BigDecimal sellingPrice;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    Product(UUID id, String name, String description, String sku, BigDecimal originalPrice,
            BigDecimal sellingPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.originalPrice = originalPrice;
        this.sellingPrice = sellingPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        // remaining fields
        // images
        // tage
        // badge
        //
    }

    public void markUpdated() {
        this.updatedAt = LocalDateTime.now();
    }

    public void validateCoreFields() {
        if (name == null || name.isBlank()) {
            throw new InvalidProductException(
                    "Product name can't be empty");
        }
        if (sku == null || sku.isBlank()) {
            throw new InvalidProductException("SKU cannot be empty");
        }
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductException("Original price must be greater than zero");
        }
        if (sellingPrice == null || sellingPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductException("Selling price must be greater than zero");
        }
    }

    public static class ProductBuilder {
        public Product build() {
            Product product = new Product(
                    this.id,
                    this.name,
                    this.description,
                    this.sku,
                    this.originalPrice,
                    this.sellingPrice,
                    this.createdAt,
                    this.updatedAt);
            product.markUpdated();
            return product;
        }
    }
}

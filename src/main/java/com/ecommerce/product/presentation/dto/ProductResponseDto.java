package com.ecommerce.product.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload representing a product")
public record ProductResponseDto(

        @Schema(description = "Unique product identifier (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Display name of the product", example = "Wireless Headphones")
        String name,

        @Schema(description = "Detailed description of the product", example = "Over-ear noise-cancelling wireless headphones")
        String description,

        @Schema(description = "Unique stock-keeping unit identifier", example = "SKU-WH-001")
        String sku,

        @Schema(description = "Original (cost/list) price", example = "120.00")
        BigDecimal originalPrice,

        @Schema(description = "Selling price shown to customers", example = "99.99")
        BigDecimal sellingPrice,

        @Schema(description = "Timestamp when the product was created", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp of the last update", example = "2024-01-20T14:45:00")
        LocalDateTime updatedAt) {
}
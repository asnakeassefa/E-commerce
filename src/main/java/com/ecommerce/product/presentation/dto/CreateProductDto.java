package com.ecommerce.product.presentation.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for creating a new product")
public record CreateProductDto(

    @Schema(description = "Display name of the product", example = "Wireless Headphones")
    @NotBlank String name,

    @Schema(description = "Detailed description of the product", example = "Over-ear noise-cancelling wireless headphones")
    @NotBlank String description,

    @Schema(description = "Unique stock-keeping unit identifier", example = "SKU-WH-001")
    @NotBlank String sku,

    @Schema(description = "Original (cost/list) price", example = "120.00")
    @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal originalPrice,

    @Schema(description = "Selling price shown to customers", example = "99.99")
    @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal sellingPrice) {
}

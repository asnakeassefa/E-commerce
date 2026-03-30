package com.ecommerce.product.presentation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.product.application.usecase.CreateProductUseCase;
import com.ecommerce.product.application.usecase.DeleteProductUseCase;
import com.ecommerce.product.application.usecase.GetProductUseCase;
import com.ecommerce.product.application.usecase.UpdateProductUseCase;
import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.presentation.dto.CreateProductDto;
import com.ecommerce.product.presentation.dto.ProductResponseDto;
import com.ecommerce.product.presentation.mapper.ProductMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Products", description = "CRUD operations for products")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
        private final CreateProductUseCase createProductUseCase;
        private final GetProductUseCase getProductUseCase;
        private final UpdateProductUseCase updateProductUseCase;
        private final DeleteProductUseCase deleteProductUseCase;
        private final ProductMapper productMapper;

        @Operation(summary = "Create a new product", description = "Adds a new product to the catalog")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation error – check request body", content = @Content)
        })
        @PostMapping
        public ResponseEntity<ProductResponseDto> createProduct(@RequestBody @Valid CreateProductDto dto) {
                Product product = createProductUseCase.execute(productMapper.toDomain(dto));

                return ResponseEntity.status(HttpStatus.CREATED).body(
                                productMapper.toResponseDto(product));
        }

        @Operation(summary = "Get product by ID", description = "Returns a single product matching the given UUID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Product found", content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProductResponseDto> getProduct(
                        @Parameter(description = "UUID of the product to retrieve", required = true) @PathVariable UUID id) {

                Product product = getProductUseCase.execute(id);

                return ResponseEntity.status(HttpStatus.OK).body(
                                productMapper.toResponseDto(product));
        }

        @Operation(summary = "List all products", description = "Returns all products in the catalog")
        @ApiResponse(responseCode = "200", description = "List of products (may be empty)", content = @Content(schema = @Schema(implementation = ProductResponseDto.class)))
        @GetMapping
        public ResponseEntity<List<ProductResponseDto>> getProducts() {

                List<Product> products = getProductUseCase.executeAll();

                return ResponseEntity.status(HttpStatus.OK).body(
                                productMapper.toResponseDtoList(products));
        }

        // update
        @PutMapping("/{id}")
        public ResponseEntity<ProductResponseDto> updateProduct(@RequestBody @Valid CreateProductDto dto,
                        @PathVariable UUID id) {
                Product product = updateProductUseCase.execute(productMapper.toDomain(dto), id);

                return ResponseEntity.status(HttpStatus.OK).body(
                                productMapper.toResponseDto(product));
        }

        // delete
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
                deleteProductUseCase.execute(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
}

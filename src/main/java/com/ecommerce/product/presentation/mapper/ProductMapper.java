package com.ecommerce.product.presentation.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.presentation.dto.CreateProductDto;
import com.ecommerce.product.presentation.dto.ProductResponseDto;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toDomain(CreateProductDto dto);

    ProductResponseDto toResponseDto(Product product);

    List<ProductResponseDto> toResponseDtoList(List<Product> products);
}

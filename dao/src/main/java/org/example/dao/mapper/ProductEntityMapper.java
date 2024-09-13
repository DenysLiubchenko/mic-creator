package org.example.dao.mapper;

import org.example.dao.entity.ProductEntity;
import org.example.domain.dto.ProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {
    ProductEntity fromDto(ProductDto productDto);
    ProductDto toDto(ProductEntity productEntity);
}

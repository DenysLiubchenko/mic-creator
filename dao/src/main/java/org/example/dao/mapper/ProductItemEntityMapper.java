package org.example.dao.mapper;

import org.example.dao.entity.ProductItemEntity;
import org.example.domain.dto.ProductItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductItemEntityMapper {
    @Mapping(target = "productId", source = "productItemEntity.id.productId")
    ProductItemDto toDto(ProductItemEntity productItemEntity);
    @Mapping(target = "id.productId", source = "productItemDto.productId")
    ProductItemEntity fromDto(ProductItemDto productItemDto);
}

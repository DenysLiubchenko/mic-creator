package org.example.service.mapper;

import org.example.dao.entity.ProductItemEntity;
import org.example.serviceapi.dto.ProductItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductItemMapper {
    @Mapping(target = "productId", source = "productItemEntity.id.productId")
    ProductItemDto toDto(ProductItemEntity productItemEntity);
}

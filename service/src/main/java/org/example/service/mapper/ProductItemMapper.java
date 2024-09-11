package org.example.service.mapper;

import org.example.dao.entity.ProductItemEntity;
import org.example.serviceapi.dto.ProductItemDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductItemMapper {
    ProductItemDto toDto(ProductItemEntity productItemDTO);
}

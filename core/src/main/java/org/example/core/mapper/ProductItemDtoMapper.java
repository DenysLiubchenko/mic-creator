package org.example.core.mapper;

import org.example.core.generated.model.ProductItemDTO;
import org.example.serviceapi.dto.ProductItemDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductItemDtoMapper {
    ProductItemDto toDto(ProductItemDTO productItemDTO);
}

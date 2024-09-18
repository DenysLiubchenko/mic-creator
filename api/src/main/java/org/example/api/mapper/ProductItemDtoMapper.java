package org.example.api.mapper;

import org.example.api.generated.model.ProductItemDTO;
import org.example.domain.dto.ProductItemDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductItemDtoMapper {
    ProductItemDto toDto(ProductItemDTO productItemDTO);
}

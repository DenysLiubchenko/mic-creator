package org.example.core.mapper;

import org.example.core.generated.model.ProductDTO;
import org.example.serviceapi.dto.ProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {
    ProductDto toDto(ProductDTO productDTO);
}

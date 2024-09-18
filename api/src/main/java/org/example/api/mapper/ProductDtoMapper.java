package org.example.api.mapper;

import org.example.api.generated.model.ProductDTO;
import org.example.domain.dto.ProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {
    ProductDto toDto(ProductDTO productDTO);
}

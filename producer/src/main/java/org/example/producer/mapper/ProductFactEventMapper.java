package org.example.producer.mapper;

import org.example.ProductFactEvent;
import org.example.domain.dto.ProductDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductFactEventMapper {
    ProductFactEvent fromDto (ProductDto productDto, String reason);
}

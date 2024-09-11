package org.example.core.mapper;

import org.example.core.generated.model.CartDTO;
import org.example.serviceapi.dto.CartDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductItemMapper.class})
public interface CartMapper {
    CartDto toDto(CartDTO cartDTO);
}

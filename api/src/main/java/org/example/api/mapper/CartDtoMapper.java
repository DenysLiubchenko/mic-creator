package org.example.api.mapper;

import org.example.api.generated.model.CartDTO;
import org.example.domain.dto.CartDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductItemDtoMapper.class})
public interface CartDtoMapper {
    CartDto toDto(CartDTO cartDTO);
    CartDto toDto(CartDTO cartDTO, Long id);
}

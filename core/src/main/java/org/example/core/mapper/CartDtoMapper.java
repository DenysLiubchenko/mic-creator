package org.example.core.mapper;

import org.example.core.generated.model.CartDTO;
import org.example.domain.dto.CartDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductItemDtoMapper.class})
public interface CartDtoMapper {
    CartDto toDto(CartDTO cartDTO);
    CartDto toDto(CartDTO cartDTO, Long id);
}

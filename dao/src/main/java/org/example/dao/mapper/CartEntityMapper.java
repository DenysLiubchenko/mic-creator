package org.example.dao.mapper;

import org.example.dao.entity.CartEntity;
import org.example.domain.dto.CartDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductItemEntityMapper.class})
public interface CartEntityMapper {
    CartDto toDto(CartEntity cartEntity);
    CartEntity fromDto(CartDto cartDto);
}

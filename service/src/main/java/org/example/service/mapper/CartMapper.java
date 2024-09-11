package org.example.service.mapper;

import org.example.dao.entity.CartEntity;
import org.example.serviceapi.dto.CartDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductItemMapper.class})
public interface CartMapper {
    CartDto toDto(CartEntity cart);
}

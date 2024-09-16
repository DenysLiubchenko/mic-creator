package org.example.producer.mapper;

import org.example.fact.CartFactEvent;
import org.example.domain.dto.CartDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartFactEventMapper {
    CartFactEvent toEvent(CartDto cartDto, String reason);
}

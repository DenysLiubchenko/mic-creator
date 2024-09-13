package org.example.producer.mapper;

import org.example.CartFactEvent;
import org.example.domain.dto.CartDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartFactEventMapper {
    CartFactEvent fromDto (CartDto cartDto, String reason);
}

package org.example.dao.mapper;

import org.example.dao.entity.DiscountEntity;
import org.example.domain.dto.DiscountDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountEntityMapper {
    DiscountEntity fromDto(DiscountDto discountDto);
    DiscountDto toDto(DiscountEntity discountEntity);
}

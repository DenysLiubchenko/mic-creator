package org.example.core.mapper;

import org.example.core.generated.model.DiscountDTO;
import org.example.domain.dto.DiscountDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountDtoMapper {
    DiscountDto toDto(DiscountDTO discountDTO);
}

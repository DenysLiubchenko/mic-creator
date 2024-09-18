package org.example.api.mapper;

import org.example.api.generated.model.DiscountDTO;
import org.example.domain.dto.DiscountDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountDtoMapper {
    DiscountDto toDto(DiscountDTO discountDTO);
}

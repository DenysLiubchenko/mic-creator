package org.example.producer.mapper;

import org.example.DiscountFactEvent;
import org.example.domain.dto.DiscountDto;
import org.mapstruct.Mapper;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface DiscountFactEventMapper {
    DiscountFactEvent fromDto(DiscountDto discountDto, String reason);

    default String map(OffsetDateTime value) {
        return value.toString();
    }
}

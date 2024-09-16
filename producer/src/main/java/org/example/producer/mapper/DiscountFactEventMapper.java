package org.example.producer.mapper;

import org.example.fact.DiscountFactEvent;
import org.example.domain.dto.DiscountDto;
import org.mapstruct.Mapper;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface DiscountFactEventMapper {
    DiscountFactEvent toEvent(DiscountDto discountDto, String reason);

    default String map(OffsetDateTime value) {
        return value.toString();
    }
}

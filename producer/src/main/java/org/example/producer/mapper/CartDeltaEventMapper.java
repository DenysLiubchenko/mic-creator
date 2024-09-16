package org.example.producer.mapper;

import org.example.delta.DeleteCartDeltaEvent;
import org.example.delta.DiscountCartDeltaEvent;
import org.example.delta.ModifyProductItemCartDeltaEvent;
import org.example.delta.RemoveProductItemCartDeltaEvent;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;
import org.example.fact.CartFactEvent;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartDeltaEventMapper {
    CartFactEvent toEvent (CartDto cartDto, String reason);
    DeleteCartDeltaEvent toEvent (Long id);
    ModifyProductItemCartDeltaEvent toEvent (Long id, List<ProductItemDto> products, String reason);
    RemoveProductItemCartDeltaEvent toEvent (Long id, List<Long> productId);
    DiscountCartDeltaEvent toDiscountEvent (Long id, List<String> discounts, String reason);
}

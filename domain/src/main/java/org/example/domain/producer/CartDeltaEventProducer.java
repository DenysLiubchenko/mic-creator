package org.example.domain.producer;

import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;

public interface CartDeltaEventProducer {
    void sendCreateEvent(CartDto cartDto);
    void sendDeleteEvent(Long cartId);
    void sendAddProductItemEvent(Long cartId, ProductItemDto... productItemDtos);
    void sendUpdateProductItemEvent(Long cartId, ProductItemDto... productItemDtos);
    void sendRemoveProductItemEvent(Long cartId, Long... productId);
    void sendAddDiscountEvent(Long cartId, String... codes);
    void sendRemoveDiscountEvent(Long cartId, String... codes);
}

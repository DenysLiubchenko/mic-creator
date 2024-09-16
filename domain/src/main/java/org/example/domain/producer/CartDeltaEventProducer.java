package org.example.domain.producer;

import org.example.domain.dto.CartDto;

public interface CartDeltaEventProducer {
    void sendCreateEvent(CartDto cartDto);
    void sendUpdateEvent(Long cartId, CartDto cartDto);
    void sendDeleteEvent(Long cartId);
}

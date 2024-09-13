package org.example.domain.producer;

import org.example.domain.dto.CartDto;

public interface CartFactEventProducer {
    void sendCreateEvent(CartDto cartDto);
    void sendUpdateEvent(Long cartId, CartDto cartDto);
    void sendDeleteEvent(Long cartId, CartDto cartDto);
}

package org.example.domain.producer;

import org.example.domain.dto.CartDto;

public interface CartFactEventProducer {
    void sendCreateEvent(CartDto cartDto);
    void sendUpdateEvent(CartDto cartDto);
    void sendDeleteEvent(CartDto cartDto);
}

package org.example.producer.producer;

import org.example.domain.dto.CartDto;
import org.example.domain.producer.CartDeltaEventProducer;
import org.springframework.stereotype.Service;

@Service
public class CartDeltaEventProducerImpl implements CartDeltaEventProducer {
    @Override
    public void sendCreateEvent(CartDto cartDto) {

    }

    @Override
    public void sendUpdateEvent(Long cartId, CartDto cartDto) {

    }

    @Override
    public void sendDeleteEvent(Long cartId) {

    }
}

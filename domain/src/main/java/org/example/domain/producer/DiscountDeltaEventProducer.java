package org.example.domain.producer;

import org.example.domain.dto.DiscountDto;

public interface DiscountDeltaEventProducer {
    void sendCreateEvent(DiscountDto discountDto);
}

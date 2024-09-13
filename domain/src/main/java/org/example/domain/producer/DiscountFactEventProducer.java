package org.example.domain.producer;

import org.example.domain.dto.DiscountDto;

public interface DiscountFactEventProducer {
    void sendCreateEvent(DiscountDto discountDto);
}

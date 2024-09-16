package org.example.producer.producer;

import org.example.domain.dto.DiscountDto;
import org.example.domain.producer.DiscountDeltaEventProducer;
import org.springframework.stereotype.Service;

@Service
public class DiscountDeltaEventProducerImpl implements DiscountDeltaEventProducer {
    @Override
    public void sendCreateEvent(DiscountDto discountDto) {

    }
}

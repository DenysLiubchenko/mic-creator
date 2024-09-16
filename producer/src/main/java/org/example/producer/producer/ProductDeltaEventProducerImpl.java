package org.example.producer.producer;

import org.example.domain.dto.ProductDto;
import org.example.domain.producer.ProductDeltaEventProducer;
import org.springframework.stereotype.Service;

@Service
public class ProductDeltaEventProducerImpl implements ProductDeltaEventProducer {
    @Override
    public void sendCreateEvent(ProductDto productDto) {

    }
}

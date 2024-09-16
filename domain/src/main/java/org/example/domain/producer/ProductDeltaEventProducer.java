package org.example.domain.producer;

import org.example.domain.dto.ProductDto;

public interface ProductDeltaEventProducer {
    void sendCreateEvent(ProductDto productDto);
}

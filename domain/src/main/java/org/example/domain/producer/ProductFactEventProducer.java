package org.example.domain.producer;

import org.example.domain.dto.ProductDto;

public interface ProductFactEventProducer {
    void sendCreateEvent(ProductDto productDto);
}

package org.example.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.producer.producer.FactEventProducer;
import org.example.domain.dto.ProductDto;
import org.example.domain.service.ProductService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    public final FactEventProducer factEventProducer;

    @Override
    public void save(ProductDto product) {
        factEventProducer.sendCreateEvent(product);
    }
}

package org.example.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.domain.dto.ProductDto;
import org.example.domain.repository.ProductRepository;
import org.example.domain.service.ProductService;
import org.example.producer.producer.ProductFactEventProducerImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    public final ProductRepository productRepository;
    public final ProductFactEventProducerImpl factEventProducer;

    @Override
    public void save(ProductDto product) {
        ProductDto savedProduct = productRepository.save(product);
        factEventProducer.sendCreateEvent(savedProduct);
    }
}

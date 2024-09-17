package org.example.service.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.dto.ProductDto;
import org.example.domain.producer.ProductDeltaEventProducer;
import org.example.domain.repository.ProductRepository;
import org.example.domain.service.ProductService;
import org.example.domain.producer.ProductFactEventProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    public final ProductRepository productRepository;
    public final ProductFactEventProducer factEventProducer;
    public final ProductDeltaEventProducer deltaEventProducer;

    @Override
    public void save(ProductDto product) {
        ProductDto savedProduct = productRepository.save(product);
        factEventProducer.sendCreateEvent(savedProduct);
        deltaEventProducer.sendCreateEvent(savedProduct);
    }
}

package org.example.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.Product;
import org.example.serviceapi.dto.ProductDto;
import org.example.serviceapi.service.ProductService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final KafkaTemplate<String, Product> productKafkaTemplate;

    @Override
    public void save(ProductDto product) {

    }
}

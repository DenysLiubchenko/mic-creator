package org.example.domain.repository;

import org.example.domain.dto.ProductDto;

public interface ProductRepository {
    ProductDto save(ProductDto productDto);
}

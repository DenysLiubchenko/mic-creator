package org.example.dao.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.adapters.ProductJpaAdapter;
import org.example.domain.dto.ProductDto;
import org.example.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaAdapter productJpaAdapter;

    @Override
    public ProductDto save(ProductDto productDto) {
        return null;
    }
}

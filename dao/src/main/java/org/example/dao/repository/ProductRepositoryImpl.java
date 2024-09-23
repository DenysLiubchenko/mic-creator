package org.example.dao.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.adapter.ProductJpaAdapter;
import org.example.dao.entity.ProductEntity;
import org.example.dao.mapper.ProductEntityMapper;
import org.example.domain.dto.ProductDto;
import org.example.domain.exception.ConflictException;
import org.example.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaAdapter productJpaAdapter;
    private final ProductEntityMapper productEntityMapper;

    @Override
    public ProductDto save(ProductDto productDto) {
        if (productJpaAdapter.existsByName(productDto.getName())) {
            throw new ConflictException("Product with name " + productDto.getName() + " already exists");
        }
        ProductEntity productEntity = productEntityMapper.fromDto(productDto);
        ProductEntity saved = productJpaAdapter.save(productEntity);
        productJpaAdapter.flush();
        log.info("Saved product: {}", productEntity);
        return productEntityMapper.toDto(saved);
    }
}

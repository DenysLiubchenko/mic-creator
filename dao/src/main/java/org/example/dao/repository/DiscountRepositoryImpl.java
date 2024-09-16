package org.example.dao.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.adapters.DiscountJpaAdapter;
import org.example.dao.entity.DiscountEntity;
import org.example.dao.mapper.DiscountEntityMapper;
import org.example.domain.dto.DiscountDto;
import org.example.domain.repository.DiscountRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountRepositoryImpl implements DiscountRepository {
    private final DiscountEntityMapper discountEntityMapper;
    private final DiscountJpaAdapter discountJpaAdapter;

    @Override
    public DiscountDto save(DiscountDto discountDto) {
        DiscountEntity discountEntity = discountEntityMapper.fromDto(discountDto);
        DiscountEntity saved = discountJpaAdapter.save(discountEntity);
        discountJpaAdapter.flush();
        log.info("Saved discount: {}", saved);
        return discountEntityMapper.toDto(saved);
    }
}

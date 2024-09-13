package org.example.dao.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.adapters.DiscountJpaAdapter;
import org.example.domain.dto.DiscountDto;
import org.example.domain.repository.DiscountRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscountRepositoryImpl implements DiscountRepository {
    private final DiscountJpaAdapter discountJpaAdapter;

    @Override
    public DiscountDto save(DiscountDto discountDto) {
        return null;
    }
}

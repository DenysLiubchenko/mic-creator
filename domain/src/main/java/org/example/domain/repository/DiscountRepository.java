package org.example.domain.repository;

import org.example.domain.dto.DiscountDto;

public interface DiscountRepository {
    DiscountDto save(DiscountDto discountDto);
}

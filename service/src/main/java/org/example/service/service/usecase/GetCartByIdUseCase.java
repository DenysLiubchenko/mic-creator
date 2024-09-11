package org.example.service.service.usecase;

import lombok.RequiredArgsConstructor;
import org.example.dao.repository.CartRepository;
import org.example.service.mapper.CartMapper;
import org.example.serviceapi.dto.CartDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCartByIdUseCase {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    public CartDto getCartById(Long id) {
        // todo: replace with custom exception
        return cartRepository.findByIdFetchDiscountsAndProductIds(id)
                .map(cartMapper::toDto)
                .orElseThrow(()->new RuntimeException("Not found"));
    }
}

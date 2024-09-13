package org.example.domain.repository;

import org.example.domain.dto.CartDto;

public interface CartRepository {
    CartDto saveCart(CartDto cart);
    CartDto deleteCart(Long cartId);
    CartDto getCartById(Long cartId);
}

package org.example.domain.service;

import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;

public interface CartService {
    void addDiscountToCartWithId(Long cartId, String code);

    void addProductToCartWithId(Long cartId, ProductItemDto productItem);

    void deleteById(Long cartId);

    void saveCart(CartDto cart);

    void updateCart(CartDto cart);

    void removeDiscountFromCartWithId(Long cartId, String code);

    void removeProductFromCartWithId(Long cartId, Long productId);
}

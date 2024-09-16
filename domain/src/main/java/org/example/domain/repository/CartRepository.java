package org.example.domain.repository;

import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;

public interface CartRepository {
    CartDto saveCart(CartDto cartDto);

    CartDto updateCart(CartDto cartDto);

    CartDto addProductToCart(Long cartId, ProductItemDto productItemDto);

    CartDto addDiscountToCart(Long cartId, String discountCode);

    CartDto deleteCart(Long cartId);

    CartDto getCartDtoById(Long cartId);

    CartDto removeDiscountFromCart(Long cartId, String code);

    CartDto removeProductFromCart(Long cartId, Long productId);
}

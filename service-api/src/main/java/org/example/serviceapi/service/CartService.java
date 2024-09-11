package org.example.serviceapi.service;

import org.example.serviceapi.dto.CartDto;
import org.example.serviceapi.dto.ProductItemDto;

public interface CartService {
    void addDiscountToCartWithId(Long cartId, String code);

    void addProductToCartWithId(Long cartId, ProductItemDto productItem);

    void deleteById(Long cartId);

    void saveCart(CartDto cart);

    void updateCart(Long cartId, CartDto cart);
}

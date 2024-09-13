package org.example.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;
import org.example.domain.repository.CartRepository;
import org.example.domain.service.CartService;
import org.example.domain.producer.CartFactEventProducer;
import org.example.domain.producer.CartDeltaEventProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
    public final CartFactEventProducer factEventProducer;
    public final CartDeltaEventProducer deltaEventProducer;
    public final CartRepository cartRepository;

    @Override
    public void deleteById(Long cartId) {
        CartDto cartDto = cartRepository.deleteCart(cartId);
        factEventProducer.sendDeleteEvent(cartId, cartDto);
    }

    @Override
    public void saveCart(CartDto cart) {
        CartDto savedCart = cartRepository.saveCart(cart);
        factEventProducer.sendCreateEvent(savedCart);
    }

    @Override
    public void updateCart(CartDto cart) {
        CartDto updatedCart = cartRepository.saveCart(cart);
        factEventProducer.sendUpdateEvent(updatedCart.getId(), updatedCart);
    }

    @Override
    public void addDiscountToCartWithId(Long cartId, String code) {
        CartDto cartById = cartRepository.getCartById(cartId);
        cartById.addDiscount(code);
        updateCart(cartById);
    }

    @Override
    public void addProductToCartWithId(Long cartId, ProductItemDto productItem) {
        CartDto cartById = cartRepository.getCartById(cartId);
        cartById.addProductItem(productItem);
        updateCart(cartById);
    }
}

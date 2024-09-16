package org.example.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;
import org.example.domain.producer.CartDeltaEventProducer;
import org.example.domain.producer.CartFactEventProducer;
import org.example.domain.repository.CartRepository;
import org.example.domain.service.CartService;
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
        factEventProducer.sendDeleteEvent(cartDto);
    }

    @Override
    public void saveCart(CartDto cart) {
        CartDto savedCart = cartRepository.saveCart(cart);
        factEventProducer.sendCreateEvent(savedCart);
    }

    @Override
    public void updateCart(CartDto cart) {
        CartDto updatedCart = cartRepository.updateCart(cart);
        factEventProducer.sendUpdateEvent(updatedCart);
    }

    @Override
    public void removeDiscountFromCartWithId(Long cartId, String code) {
        CartDto cartDto = cartRepository.removeDiscountFromCart(cartId, code);
        factEventProducer.sendUpdateEvent(cartDto);
    }

    @Override
    public void addDiscountToCartWithId(Long cartId, String code) {
        CartDto cartDto = cartRepository.addDiscountToCart(cartId, code);
        factEventProducer.sendUpdateEvent(cartDto);
    }

    @Override
    public void removeProductFromCartWithId(Long cartId, Long productId) {
        CartDto cartDto = cartRepository.removeProductFromCart(cartId, productId);
        factEventProducer.sendUpdateEvent(cartDto);
    }

    @Override
    public void addProductToCartWithId(Long cartId, ProductItemDto productItem) {
        CartDto cartDto = cartRepository.addProductToCart(cartId, productItem);
        factEventProducer.sendUpdateEvent(cartDto);
    }
}

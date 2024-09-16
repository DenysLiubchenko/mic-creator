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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        deltaEventProducer.sendDeleteEvent(cartId);
    }

    @Override
    public void saveCart(CartDto cart) {
        CartDto savedCart = cartRepository.saveCart(cart);
        factEventProducer.sendCreateEvent(savedCart);
        deltaEventProducer.sendCreateEvent(savedCart);
    }

    @Override
    public void updateCart(CartDto cart) {
        Long cartId = cart.getId();
        CartDto oldCart = cartRepository.getCartDtoById(cartId);
        CartDto updatedCart = cartRepository.updateCart(cart);
        factEventProducer.sendUpdateEvent(updatedCart);

        Set<String> addedDiscounts = new HashSet<>(updatedCart.getDiscounts());
        addedDiscounts.removeAll(oldCart.getDiscounts());
        Set<String> removedDiscounts = new HashSet<>(oldCart.getDiscounts());
        removedDiscounts.removeAll(updatedCart.getDiscounts());

        if (!addedDiscounts.isEmpty()) {
            deltaEventProducer.sendAddDiscountEvent(cartId, addedDiscounts.toArray(new String[0]));
        }
        if (!removedDiscounts.isEmpty()) {
            deltaEventProducer.sendRemoveDiscountEvent(cartId, removedDiscounts.toArray(new String[0]));
        }

        Set<ProductItemDto> addedProducts = new HashSet<>(updatedCart.getProducts());
        addedProducts.removeAll(oldCart.getProducts());
        Set<ProductItemDto> removedProducts = new HashSet<>(oldCart.getProducts());
        removedProducts.removeAll(updatedCart.getProducts());

        if (!addedProducts.isEmpty()) {
            Set<ProductItemDto> newProducts = new HashSet<>(addedProducts).stream()
                    .filter(productItemDto -> oldCart.getProducts().stream()
                            .anyMatch(p -> p.getProductId().equals(productItemDto.getProductId())))
                    .collect(Collectors.toSet());

            Set<ProductItemDto> updatedProducts = new HashSet<>(addedProducts);
            addedProducts.removeAll(newProducts);

            if (!newProducts.isEmpty()) {
                deltaEventProducer.sendAddProductItemEvent(cartId, newProducts.toArray(new ProductItemDto[0]));
            }
            if (!updatedProducts.isEmpty()) {
                deltaEventProducer.sendUpdateProductItemEvent(cartId, updatedProducts.toArray(new ProductItemDto[0]));
            }
        }
        if (!removedProducts.isEmpty()) {
            deltaEventProducer.sendRemoveProductItemEvent(cartId, removedProducts.stream()
                    .map(ProductItemDto::getProductId).toArray(Long[]::new));
        }
    }

    @Override
    public void removeDiscountFromCartWithId(Long cartId, String code) {
        CartDto cartDto = cartRepository.removeDiscountFromCart(cartId, code);
        factEventProducer.sendUpdateEvent(cartDto);
        deltaEventProducer.sendRemoveDiscountEvent(cartId, code);
    }

    @Override
    public void addDiscountToCartWithId(Long cartId, String code) {
        CartDto cartDto = cartRepository.addDiscountToCart(cartId, code);
        factEventProducer.sendUpdateEvent(cartDto);
        deltaEventProducer.sendAddDiscountEvent(cartId, code);
    }

    @Override
    public void removeProductFromCartWithId(Long cartId, Long productId) {
        CartDto cartDto = cartRepository.removeProductFromCart(cartId, productId);
        factEventProducer.sendUpdateEvent(cartDto);
        deltaEventProducer.sendRemoveProductItemEvent(cartId, productId);
    }

    @Override
    public void addProductToCartWithId(Long cartId, ProductItemDto productItem) {
        CartDto cartDto = cartRepository.addProductToCart(cartId, productItem);
        factEventProducer.sendUpdateEvent(cartDto);
        deltaEventProducer.sendAddProductItemEvent(cartId, productItem);
    }
}

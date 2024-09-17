package org.example.service.service;

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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
        CartDto oldCart = cartRepository.getCartDtoById(cart.getId());
        CartDto updatedCart = cartRepository.updateCart(cart);
        factEventProducer.sendUpdateEvent(updatedCart);

        sendDeltaUpdateForDiscounts(oldCart, updatedCart);
        sendDeltaUpdatesForProducts(oldCart, updatedCart);
    }

    private void sendDeltaUpdatesForProducts(CartDto oldCart, CartDto newCart) {
        Set<ProductItemDto> newProducts = new HashSet<>();
        Set<ProductItemDto> modifiedProducts = new HashSet<>();
        Set<ProductItemDto> removedProducts = new HashSet<>(oldCart.getProducts());

        Map<Long, ProductItemDto> oldProductMap = oldCart.getProducts().stream()
                .collect(Collectors.toMap(ProductItemDto::getProductId, Function.identity()));

        for (ProductItemDto updatedProduct : newCart.getProducts()) {
            ProductItemDto oldProduct = oldProductMap.get(updatedProduct.getProductId());

            if (oldProduct == null) {
                newProducts.add(updatedProduct);
            } else if (!oldProduct.getQuantity().equals(updatedProduct.getQuantity())) {
                modifiedProducts.add(updatedProduct);
            }
            removedProducts.remove(oldProduct);
        }

        if (!newProducts.isEmpty()) {
            deltaEventProducer.sendAddProductItemEvent(oldCart.getId(), newProducts.toArray(new ProductItemDto[0]));
        }
        if (!modifiedProducts.isEmpty()) {
            deltaEventProducer.sendUpdateProductItemEvent(oldCart.getId(), modifiedProducts.toArray(new ProductItemDto[0]));
        }
        if (!removedProducts.isEmpty()) {
            deltaEventProducer.sendRemoveProductItemEvent(oldCart.getId(), removedProducts.stream()
                    .map(ProductItemDto::getProductId).toArray(Long[]::new));
        }
    }

    private void sendDeltaUpdateForDiscounts(CartDto oldCart, CartDto newCart) {
        Set<String> addedDiscounts = new HashSet<>(newCart.getDiscounts());
        addedDiscounts.removeAll(oldCart.getDiscounts());
        Set<String> removedDiscounts = new HashSet<>(oldCart.getDiscounts());
        removedDiscounts.removeAll(newCart.getDiscounts());

        if (!addedDiscounts.isEmpty()) {
            deltaEventProducer.sendAddDiscountEvent(newCart.getId(), addedDiscounts.toArray(new String[0]));
        }
        if (!removedDiscounts.isEmpty()) {
            deltaEventProducer.sendRemoveDiscountEvent(newCart.getId(), removedDiscounts.toArray(new String[0]));
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

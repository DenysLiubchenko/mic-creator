package org.example.core.controller;

import lombok.RequiredArgsConstructor;
import org.example.core.generated.api.CartApi;
import org.example.core.generated.model.CartDTO;
import org.example.core.generated.model.ProductItemDTO;
import org.example.core.mapper.CartDtoMapper;
import org.example.core.mapper.ProductItemDtoMapper;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;
import org.example.domain.service.CartService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class CartController implements CartApi {
    private final CartService cartService;
    private final CartDtoMapper cartDtoMapper;
    private final ProductItemDtoMapper productItemDtoMapper;

    @Override
    public void addDiscountToCart(Long cartId, String code) {
        cartService.addDiscountToCartWithId(cartId, code);
    }

    @Override
    public void removeDiscountFromCart(Long cartId, String code) {
        cartService.removeDiscountFromCartWithId(cartId, code);
    }

    @Override
    public void addProductToCart(Long cartId, ProductItemDTO productItemDTO) {
        ProductItemDto productItem = productItemDtoMapper.toDto(productItemDTO);
        cartService.addProductToCartWithId(cartId, productItem);
    }

    @Override
    public void removeProductFromCart(Long cartId, Long productId) {
        cartService.removeProductFromCartWithId(cartId, productId);
    }

    @Override
    public void deleteCart(Long cartId) {
        cartService.deleteById(cartId);
    }

    @Override
    public void saveCart(CartDTO cartDTO) {
        CartDto cart = cartDtoMapper.toDto(cartDTO);
        cartService.saveCart(cart);
    }

    @Override
    public void updateCart(Long cartId, CartDTO cartDTO) {
        CartDto cart = cartDtoMapper.toDto(cartDTO, cartId);
        cartService.updateCart(cart);
    }
}

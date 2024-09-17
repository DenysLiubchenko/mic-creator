package org.example.service.service;

import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;
import org.example.domain.producer.CartDeltaEventProducer;
import org.example.domain.producer.CartFactEventProducer;
import org.example.domain.repository.CartRepository;
import org.example.service.ModelUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.example.service.ModelUtils.getProductItemDto;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartFactEventProducer factEventProducer;

    @Mock
    private CartDeltaEventProducer deltaEventProducer;

    @InjectMocks
    private CartServiceImpl cartService;

    private final CartDto cartDto = ModelUtils.getCartDto();
    private final ProductItemDto productItemDto = getProductItemDto();

    @Test
    void deleteByIdTest() {
        // Given
        Long cartId = 1L;
        given(cartRepository.deleteCart(cartId)).willReturn(cartDto);
        willDoNothing().given(factEventProducer).sendDeleteEvent(cartDto);
        willDoNothing().given(deltaEventProducer).sendDeleteEvent(cartId);

        // When
        cartService.deleteById(cartId);

        // Then
        then(cartRepository).should().deleteCart(cartId);
        then(factEventProducer).should().sendDeleteEvent(cartDto);
        then(deltaEventProducer).should().sendDeleteEvent(cartId);
    }

    @Test
    void saveCartTest() {
        // Given
        given(cartRepository.saveCart(cartDto)).willReturn(cartDto);
        willDoNothing().given(factEventProducer).sendCreateEvent(cartDto);
        willDoNothing().given(deltaEventProducer).sendCreateEvent(cartDto);

        // When
        cartService.saveCart(cartDto);

        // Then
        then(cartRepository).should().saveCart(cartDto);
        then(factEventProducer).should().sendCreateEvent(cartDto);
        then(deltaEventProducer).should().sendCreateEvent(cartDto);
    }

    @Test
    void updateCartTest() {
        // Given
        Long cartId = cartDto.getId();
        CartDto newCart = ModelUtils.getCartDto();
        String removedDiscount = "CODE_2000";
        String addedDiscount = "CODE_2005";
        newCart.setDiscounts(Set.of("CODE_2001","CODE_2002", addedDiscount));

        ProductItemDto removedProductItemDto = getProductItemDto(1L,1);
        ProductItemDto modifiedProductItemDto = getProductItemDto(2L,22);
        ProductItemDto addedproductItemDto = getProductItemDto(55L,1);
        newCart.setProducts(Set.of(modifiedProductItemDto, getProductItemDto(3L,3), addedproductItemDto));

        given(cartRepository.getCartDtoById(cartId)).willReturn(cartDto);
        given(cartRepository.updateCart(newCart)).willReturn(newCart);
        willDoNothing().given(factEventProducer).sendUpdateEvent(newCart);

        willDoNothing().given(deltaEventProducer).sendAddDiscountEvent(cartId, addedDiscount);
        willDoNothing().given(deltaEventProducer).sendRemoveDiscountEvent(cartId, removedDiscount);
        willDoNothing().given(deltaEventProducer).sendAddProductItemEvent(cartId, addedproductItemDto);
        willDoNothing().given(deltaEventProducer).sendRemoveProductItemEvent(cartId, removedProductItemDto.getProductId());
        willDoNothing().given(deltaEventProducer).sendUpdateProductItemEvent(cartId, modifiedProductItemDto);

        // When
        cartService.updateCart(newCart);

        // Then
        then(cartRepository).should().getCartDtoById(cartId);
        then(cartRepository).should().updateCart(newCart);
        then(factEventProducer).should().sendUpdateEvent(newCart);
        then(deltaEventProducer).should().sendAddDiscountEvent(cartId, addedDiscount);
        then(deltaEventProducer).should().sendRemoveDiscountEvent(cartId, removedDiscount);
        then(deltaEventProducer).should().sendAddProductItemEvent(cartId, addedproductItemDto);
        then(deltaEventProducer).should().sendRemoveProductItemEvent(cartId, removedProductItemDto.getProductId());
        then(deltaEventProducer).should().sendUpdateProductItemEvent(cartId, modifiedProductItemDto);
    }

    @Test
    void addProductToCartTest() {
        // Given
        Long cartId = 1L;
        given(cartRepository.addProductToCart(cartId, productItemDto)).willReturn(cartDto);
        willDoNothing().given(factEventProducer).sendUpdateEvent(cartDto);
        willDoNothing().given(deltaEventProducer).sendAddProductItemEvent(cartId, productItemDto);

        // When
        cartService.addProductToCartWithId(cartId, productItemDto);

        // Then
        then(cartRepository).should().addProductToCart(cartId, productItemDto);
        then(factEventProducer).should().sendUpdateEvent(cartDto);
        then(deltaEventProducer).should().sendAddProductItemEvent(cartId, productItemDto);
    }

    @Test
    void removeDiscountFromCartTest() {
        // Given
        Long cartId = 1L;
        String discountCode = "DISCOUNT1";
        given(cartRepository.removeDiscountFromCart(cartId, discountCode)).willReturn(cartDto);
        willDoNothing().given(factEventProducer).sendUpdateEvent(cartDto);
        willDoNothing().given(deltaEventProducer).sendRemoveDiscountEvent(cartId, discountCode);

        // When
        cartService.removeDiscountFromCartWithId(cartId, discountCode);

        // Then
        then(cartRepository).should().removeDiscountFromCart(cartId, discountCode);
        then(factEventProducer).should().sendUpdateEvent(cartDto);
        then(deltaEventProducer).should().sendRemoveDiscountEvent(cartId, discountCode);
    }

    @Test
    void addDiscountToCartTest() {
        // Given
        Long cartId = 1L;
        String discountCode = "DISCOUNT1";
        given(cartRepository.addDiscountToCart(cartId, discountCode)).willReturn(cartDto);
        willDoNothing().given(factEventProducer).sendUpdateEvent(cartDto);
        willDoNothing().given(deltaEventProducer).sendAddDiscountEvent(cartId, discountCode);

        // When
        cartService.addDiscountToCartWithId(cartId, discountCode);

        // Then
        then(cartRepository).should().addDiscountToCart(cartId, discountCode);
        then(factEventProducer).should().sendUpdateEvent(cartDto);
        then(deltaEventProducer).should().sendAddDiscountEvent(cartId, discountCode);
    }

    @Test
    void removeProductFromCartTest() {
        // Given
        Long cartId = 1L;
        Long productId = 101L;
        given(cartRepository.removeProductFromCart(cartId, productId)).willReturn(cartDto);
        willDoNothing().given(factEventProducer).sendUpdateEvent(cartDto);
        willDoNothing().given(deltaEventProducer).sendRemoveProductItemEvent(cartId, productId);

        // When
        cartService.removeProductFromCartWithId(cartId, productId);

        // Then
        then(cartRepository).should().removeProductFromCart(cartId, productId);
        then(factEventProducer).should().sendUpdateEvent(cartDto);
        then(deltaEventProducer).should().sendRemoveProductItemEvent(cartId, productId);
    }
}

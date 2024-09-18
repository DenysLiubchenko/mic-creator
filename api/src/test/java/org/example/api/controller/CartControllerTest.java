package org.example.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.api.ModelUtils;
import org.example.api.handler.CustomExceptionHandler;
import org.example.api.mapper.CartDtoMapper;
import org.example.api.mapper.ProductItemDtoMapper;
import org.example.domain.exception.NotFoundException;
import org.example.domain.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@ContextConfiguration(classes = {CartController.class})
@Import(value = {AopAutoConfiguration.class, CustomExceptionHandler.class})
public class CartControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CartService cartService;
    @MockBean
    private CartDtoMapper cartDtoMapper;
    @MockBean
    private ProductItemDtoMapper productItemDtoMapper;

    @Test
    void addDiscountToCartTest() throws Exception {
        // Given
        Long cartId = 1L;
        String discountCode = "DISCOUNT123";

        willDoNothing().given(cartService).addDiscountToCartWithId(cartId, discountCode);

        // When
        mockMvc.perform(patch("/cart/{cartId}/discount", cartId)
                        .param("code", discountCode))
                .andExpect(status().isNoContent());

        // Then
        then(cartService).should().addDiscountToCartWithId(cartId, discountCode);
    }

    @Test
    void addDiscountToCartSendsNotFoundTest() throws Exception {
        // Given
        Long cartId = 1L;
        String discountCode = "DISCOUNT123";

        willThrow(NotFoundException.class).given(cartService).addDiscountToCartWithId(cartId, discountCode);

        // When
        mockMvc.perform(patch("/cart/{cartId}/discount", cartId)
                        .param("code", discountCode))
                .andExpect(status().isNotFound());

        // Then
        then(cartService).should().addDiscountToCartWithId(cartId, discountCode);
    }

    @Test
    void removeDiscountFromCartTest() throws Exception {
        // Given
        Long cartId = 1L;
        String discountCode = "DISCOUNT123";

        willDoNothing().given(cartService).removeDiscountFromCartWithId(cartId, discountCode);

        // When
        mockMvc.perform(patch("/cart/{cartId}/discount/remove", cartId)
                        .param("code", discountCode))
                .andExpect(status().isNoContent());

        // Then
        then(cartService).should().removeDiscountFromCartWithId(cartId, discountCode);
    }

    @Test
    void addProductToCartTest() throws Exception {
        // Given
        Long cartId = 1L;
        var productItemDTO = ModelUtils.getProductItemDTO();
        var productItemDto = ModelUtils.getProductItemDto();
        given(productItemDtoMapper.toDto(productItemDTO)).willReturn(productItemDto);

        willDoNothing().given(cartService).addProductToCartWithId(cartId, productItemDto);

        // When
        mockMvc.perform(patch("/cart/{cartId}/product", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productItemDTO)))
                .andExpect(status().isNoContent());

        // Then
        then(productItemDtoMapper).should().toDto(productItemDTO);
        then(cartService).should().addProductToCartWithId(cartId, productItemDto);
    }

    @Test
    void removeProductFromCartTest() throws Exception {
        // Given
        Long cartId = 1L;
        Long productId = 2L;

        willDoNothing().given(cartService).removeProductFromCartWithId(cartId, productId);

        // When
        mockMvc.perform(patch("/cart/{cartId}/product/remove", cartId)
                        .param("productId", productId.toString()))
                .andExpect(status().isNoContent());

        // Then
        then(cartService).should().removeProductFromCartWithId(cartId, productId);
    }

    @Test
    void deleteCartTest() throws Exception {
        // Given
        Long cartId = 1L;
        willDoNothing().given(cartService).deleteById(cartId);

        // When
        mockMvc.perform(delete("/cart/{cartId}", cartId))
                .andExpect(status().isNoContent());

        // Then
        then(cartService).should().deleteById(cartId);
    }

    @Test
    void saveCartTest() throws Exception {
        // Given
        var cartDTO = ModelUtils.getCartDTO();
        var cartDto = ModelUtils.getCartDto();
        given(cartDtoMapper.toDto(cartDTO)).willReturn(cartDto);
        willDoNothing().given(cartService).saveCart(cartDto);

        // When
        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartDTO)))
                .andExpect(status().isCreated());

        // Then
        then(cartDtoMapper).should().toDto(cartDTO);
        then(cartService).should().saveCart(cartDto);
    }

    @Test
    void updateCartTest() throws Exception {
        // Given
        Long cartId = 1L;
        var cartDTO = ModelUtils.getCartDTO();
        var cartDto = ModelUtils.getCartDto();
        given(cartDtoMapper.toDto(cartDTO, cartId)).willReturn(cartDto);
        willDoNothing().given(cartService).updateCart(cartDto);

        // When
        mockMvc.perform(put("/cart/{cartId}", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartDTO)))
                .andExpect(status().isNoContent());

        // Then
        then(cartDtoMapper).should().toDto(cartDTO, cartId);
        then(cartService).should().updateCart(cartDto);
    }
}

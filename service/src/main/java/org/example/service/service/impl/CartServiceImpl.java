package org.example.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.producer.producer.DeltaEventProducer;
import org.example.producer.producer.FactEventProducer;
import org.example.service.service.usecase.GetCartByIdUseCase;
import org.example.serviceapi.dto.CartDto;
import org.example.serviceapi.dto.ProductItemDto;
import org.example.serviceapi.service.CartService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    public final FactEventProducer factEventProducer;
    public final DeltaEventProducer deltaEventProducer;
    public final GetCartByIdUseCase getCartByIdUseCase;

    @Override
    public void deleteById(Long cartId) {

    }

    @Override
    public void saveCart(CartDto cart) {

    }

    @Override
    public void updateCart(Long cartId, CartDto cart) {

    }

    @Override
    public void addDiscountToCartWithId(Long cartId, String code) {

    }

    @Override
    public void addProductToCartWithId(Long cartId, ProductItemDto productItem) {

    }
}

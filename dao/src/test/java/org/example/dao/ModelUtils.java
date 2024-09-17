package org.example.dao;

import org.example.dao.entity.CartEntity;
import org.example.dao.entity.DiscountEntity;
import org.example.dao.entity.ProductEntity;
import org.example.dao.entity.ProductItemEntity;
import org.example.dao.entity.ProductItemId;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.DiscountDto;
import org.example.domain.dto.ProductDto;
import org.example.domain.dto.ProductItemDto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelUtils {
    public static DiscountDto getDiscountDto() {
        return DiscountDto.builder().code("CODE_2000")
                .due(OffsetDateTime.of(3000, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC)).build();
    }

    public static ProductDto getProductDto() {
        return ProductDto.builder().name("productName").cost(BigDecimal.TEN).build();
    }

    public static ProductItemDto getProductItemDto() {
        return ProductItemDto.builder().productId(1L).quantity(1).build();
    }

    public static ProductItemDto getProductItemDto(Long productId, Integer quantity) {
        return ProductItemDto.builder().productId(productId).quantity(quantity).build();
    }

    public static CartDto getCartDto() {
        return CartDto.builder()
                .id(1L)
                .discounts(Set.of("CODE_2000","CODE_2001","CODE_2002"))
                .products(Set.of(getProductItemDto(1L,1), getProductItemDto(2L,2), getProductItemDto(3L,3)))
                .build();
    }

    public static CartEntity getCartEntity() {
        HashSet<ProductItemEntity> pi = new HashSet<>(List.of(getProductItemEntity()));
        HashSet<String> d = new HashSet<>(List.of("CODE_2000", "CODE_2001", "CODE_2002"));
        return CartEntity.builder()
                .id(1L)
                .discounts(d)
                .products(pi)
                .build();
    }

    public static ProductItemEntity getProductItemEntity() {
        return ProductItemEntity.builder()
                .id(new ProductItemId(1L,1L))
                .quantity(12)
                .cart(new CartEntity(1L, null, null))
                .build();
    }

    public static DiscountEntity getDiscountEntity() {
        return DiscountEntity.builder()
                .code("CODE_2000")
                .due(OffsetDateTime.of(3000, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC)).build();
    }

    public static ProductEntity getProductEntity() {
        return ProductEntity.builder()
                .id(1L)
                .cost(BigDecimal.TEN)
                .name("productName")
                .build();
    }
}

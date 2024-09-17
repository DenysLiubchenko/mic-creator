package org.example.producer;

import org.example.ProductItem;
import org.example.delta.DeleteCartDeltaEvent;
import org.example.delta.DiscountCartDeltaEvent;
import org.example.delta.ModifyProductItemCartDeltaEvent;
import org.example.delta.RemoveProductItemCartDeltaEvent;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.DiscountDto;
import org.example.domain.dto.ProductDto;
import org.example.domain.dto.ProductItemDto;
import org.example.fact.CartFactEvent;
import org.example.fact.DiscountFactEvent;
import org.example.fact.ProductFactEvent;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

    public static ProductFactEvent getProductFactEvent(String reason) {
        return ProductFactEvent.newBuilder()
                .setId(1)
                .setReason(reason)
                .setName("productName")
                .setCost("22.22")
                .build();
    }

    public static DiscountFactEvent getDiscountFactEvent(String reason) {
        return DiscountFactEvent.newBuilder()
                .setReason(reason)
                .setCode("CODE_2000")
                .setDue("2030-12-03T10:15:30+01:00")
                .build();
    }

    public static CartFactEvent getCartFactEvent(String reason) {
        return CartFactEvent.newBuilder()
                .setId(1)
                .setReason(reason)
                .setDiscounts(List.of("CODE_2000"))
                .setProducts(List.of(getProductItem()))
                .build();
    }

    public static ProductItem getProductItem() {
        return ProductItem.newBuilder()
                .setProductId(1L)
                .setQuantity(1)
                .build();
    }

    public static DeleteCartDeltaEvent getDeleteCartDeltaEvent() {
        return DeleteCartDeltaEvent.newBuilder()
                .setId(1)
                .build();
    }

    public static ModifyProductItemCartDeltaEvent getModifyProductItemCartDeltaEvent(String reason) {
        return ModifyProductItemCartDeltaEvent.newBuilder()
                .setId(1)
                .setProducts(List.of(getProductItem()))
                .setReason(reason)
                .build();
    }

    public static RemoveProductItemCartDeltaEvent getRemoveProductItemCartDeltaEvent() {
        return RemoveProductItemCartDeltaEvent.newBuilder()
                .setId(1)
                .setProductIds(List.of(1L))
                .build();
    }

    public static DiscountCartDeltaEvent getDiscountCartDeltaEvent(String reason) {
        return DiscountCartDeltaEvent.newBuilder()
                .setId(1)
                .setReason(reason)
                .setDiscounts(List.of("CODE_2000"))
                .build();
    }
}

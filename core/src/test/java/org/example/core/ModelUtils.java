package org.example.core;

import org.example.core.generated.model.DiscountDTO;
import org.example.core.generated.model.ProductDTO;
import org.example.core.generated.model.CartDTO;
import org.example.core.generated.model.ProductItemDTO;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.DiscountDto;
import org.example.domain.dto.ProductDto;
import org.example.domain.dto.ProductItemDto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;

public class ModelUtils {
    public static DiscountDTO getDiscountDTO() {
        return new DiscountDTO("CODE_2000", OffsetDateTime.of(3000, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC));
    }

    public static DiscountDto getDiscountDto() {
        return DiscountDto.builder().code("CODE_2000")
                .due(OffsetDateTime.of(3000, 1, 1, 1, 1, 1, 1, ZoneOffset.UTC)).build();
    }

    public static ProductDTO getProductDTO() {
        return new ProductDTO("productName", BigDecimal.TEN);
    }

    public static ProductDto getProductDto() {
        return ProductDto.builder().name("productName").cost(BigDecimal.TEN).build();
    }

    public static ProductItemDTO getProductItemDTO() {
        return new ProductItemDTO(1L);
    }

    public static ProductItemDto getProductItemDto() {
        return ProductItemDto.builder().productId(1L).quantity(1).build();
    }

    public static CartDTO getCartDTO() {
        CartDTO cartDTO = new CartDTO();
        cartDTO.discounts(Set.of("CODE_2000"));
        cartDTO.products(Set.of(getProductItemDTO()));
        return cartDTO;
    }

    public static CartDto getCartDto() {
        return CartDto.builder().discounts(Set.of("CODE_2000")).products(Set.of(getProductItemDto())).build();
    }
}

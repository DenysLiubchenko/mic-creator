package org.example.boot;

import org.example.api.generated.model.CartDTO;
import org.example.api.generated.model.DiscountDTO;
import org.example.api.generated.model.ProductDTO;
import org.example.api.generated.model.ProductItemDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public class ModelUtils {
    public static DiscountDTO getDiscountDTO() {
        return new DiscountDTO("CODE_2000", OffsetDateTime.parse("3000-01-01T01:01:01Z"));
    }

    public static ProductDTO getProductDTO() {
        return new ProductDTO("productName", BigDecimal.valueOf(99.99));
    }

    public static ProductItemDTO getProductItemDTO() {
        return new ProductItemDTO(1L);
    }

    public static CartDTO getCartDTO() {
        CartDTO cartDTO = new CartDTO();
        cartDTO.discounts(Set.of("CODE_2000"));
        cartDTO.products(Set.of(getProductItemDTO()));
        return cartDTO;
    }
}

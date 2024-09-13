package org.example.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    private Long id;
    @Builder.Default
    private Set<ProductItemDto> products = new HashSet<>();
    @Builder.Default
    private Set<String> discounts = new HashSet<>();

    public boolean addProductItem(ProductItemDto productItemDto) {
        return products.add(productItemDto);
    }

    public boolean removeProductItem(Long productId) {
        Optional<ProductItemDto> productItemDto = products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst();
        return productItemDto
                .filter(itemDto -> products.remove(itemDto))
                .isPresent();
    }

    public boolean addDiscount(String discount) {
        return discounts.add(discount);
    }

    public boolean removeDiscount(String discount) {
        return discounts.remove(discount);
    }
}

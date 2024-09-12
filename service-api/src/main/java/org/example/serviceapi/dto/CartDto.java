package org.example.serviceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    @Builder.Default
    private Set<ProductItemDto> products = new HashSet<>();
    @Builder.Default
    private Set<String> discounts = new HashSet<>();
}

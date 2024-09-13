package org.example.dao.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "carts")
@Getter
@Setter
@EqualsAndHashCode(exclude = {"products", "discounts"})
@ToString(exclude = {"products", "discounts"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    @Setter(AccessLevel.PRIVATE)
    @Builder.Default
    private Set<ProductItemEntity> products = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "carts_discounts", joinColumns = @JoinColumn(name = "cart_id"))
    @Column(name = "discount_code")
    @Setter(AccessLevel.PRIVATE)
    @Builder.Default
    private Set<String> discounts = new HashSet<>();
}

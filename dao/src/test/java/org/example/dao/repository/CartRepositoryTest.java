package org.example.dao.repository;

import org.example.dao.ModelUtils;
import org.example.dao.adapters.CartJpaAdapter;
import org.example.dao.adapters.DiscountJpaAdapter;
import org.example.dao.adapters.ProductJpaAdapter;
import org.example.dao.entity.CartEntity;
import org.example.dao.entity.ProductItemEntity;
import org.example.dao.entity.ProductItemId;
import org.example.dao.mapper.CartEntityMapper;
import org.example.dao.mapper.ProductItemEntityMapper;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;
import org.example.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CartRepositoryTest {

    @Mock
    private CartJpaAdapter cartJpaAdapter;

    @Mock
    private ProductJpaAdapter productJpaAdapter;

    @Mock
    private DiscountJpaAdapter discountJpaAdapter;

    @Mock
    private CartEntityMapper cartEntityMapper;

    @Mock
    private ProductItemEntityMapper productItemEntityMapper;

    @InjectMocks
    private CartRepositoryImpl cartRepository;

    private final CartEntity cartEntity = ModelUtils.getCartEntity();
    private final CartDto cartDto = ModelUtils.getCartDto();
    private final ProductItemDto productItemDto = ModelUtils.getProductItemDto();

    @Test
    void addProductToCartTest() {
        // Given
        Long cartId = cartDto.getId();
        given(productJpaAdapter.existsById(productItemDto.getProductId())).willReturn(true);
        given(cartJpaAdapter.findByIdFetchDiscountsAndProductIds(cartId)).willReturn(Optional.of(cartEntity));
        given(cartEntityMapper.toDto(cartEntity)).willReturn(cartDto);

        // When
        CartDto result = cartRepository.addProductToCart(cartId, productItemDto);

        // Then
        then(productJpaAdapter).should().existsById(productItemDto.getProductId());
        then(cartJpaAdapter).should().findByIdFetchDiscountsAndProductIds(cartId);
        then(cartJpaAdapter).should().flush();
        then(cartEntityMapper).should().toDto(cartEntity);
        assertThat(result).isEqualTo(cartDto);
    }

    @Test
    void addProductToCart_withNewProductTest() {
        // Given
        ProductItemDto productItemDto = ModelUtils.getProductItemDto();
        productItemDto.setProductId(3L);
        ProductItemEntity productItemEntity = ModelUtils.getProductItemEntity();
        productItemEntity.getId().setProductId(3L);
        Long cartId = cartDto.getId();
        given(productJpaAdapter.existsById(productItemDto.getProductId())).willReturn(true);
        given(productItemEntityMapper.fromDto(productItemDto)).willReturn(productItemEntity);
        given(cartJpaAdapter.findByIdFetchDiscountsAndProductIds(cartId)).willReturn(Optional.of(cartEntity));
        given(cartEntityMapper.toDto(cartEntity)).willReturn(cartDto);

        // When
        CartDto result = cartRepository.addProductToCart(cartId, productItemDto);

        // Then
        then(productJpaAdapter).should().existsById(productItemDto.getProductId());
        then(cartJpaAdapter).should().findByIdFetchDiscountsAndProductIds(cartId);
        then(cartJpaAdapter).should().flush();
        then(cartEntityMapper).should().toDto(cartEntity);
        assertThat(result).isEqualTo(cartDto);
    }

    @Test
    void addProductToCart_whenProductNotFound_throwsNotFoundExceptionTest() {
        // Given
        Long cartId = cartDto.getId();
        given(productJpaAdapter.existsById(productItemDto.getProductId())).willReturn(false);

        // When
        assertThatThrownBy(() -> cartRepository.addProductToCart(cartId, productItemDto))
                .isInstanceOf(NotFoundException.class);

        // Then
        then(productJpaAdapter).should().existsById(productItemDto.getProductId());
    }

    @Test
    void removeProductFromCartTest() {
        // Given
        Long cartId = cartDto.getId();
        Long productId = 1L;
        given(productJpaAdapter.existsById(productId)).willReturn(true);
        given(cartJpaAdapter.findByIdFetchDiscountsAndProductIds(cartId)).willReturn(Optional.of(cartEntity));
        given(cartEntityMapper.toDto(cartEntity)).willReturn(cartDto);

        // When
        CartDto result = cartRepository.removeProductFromCart(cartId, productId);

        // Then
        then(productJpaAdapter).should().existsById(productId);
        then(cartJpaAdapter).should().findByIdFetchDiscountsAndProductIds(cartId);
        then(cartJpaAdapter).should().flush();
        then(cartEntityMapper).should().toDto(cartEntity);
        assertThat(result).isEqualTo(cartDto);
    }

    @Test
    void removeProductFromCart_whenProductNotFound_throwsNotFoundExceptionTest() {
        // Given
        Long productId = 1L;
        given(productJpaAdapter.existsById(productId)).willReturn(false);

        // When
        assertThatThrownBy(() -> cartRepository.removeProductFromCart(cartDto.getId(), productId))
                .isInstanceOf(NotFoundException.class);

        // Then
        then(productJpaAdapter).should().existsById(productId);
    }

    @Test
    void addDiscountToCartTest() {
        // Given
        Long cartId = cartDto.getId();
        String discountCode = "DISCOUNT1";
        given(discountJpaAdapter.existsById(discountCode)).willReturn(true);
        given(cartJpaAdapter.findByIdFetchDiscountsAndProductIds(cartId)).willReturn(Optional.of(cartEntity));
        given(cartEntityMapper.toDto(cartEntity)).willReturn(cartDto);

        // When
        CartDto result = cartRepository.addDiscountToCart(cartId, discountCode);

        // Then
        then(discountJpaAdapter).should().existsById(discountCode);
        then(cartJpaAdapter).should().findByIdFetchDiscountsAndProductIds(cartId);
        then(cartJpaAdapter).should().flush();
        then(cartEntityMapper).should().toDto(cartEntity);
        assertThat(result).isEqualTo(cartDto);
    }

    @Test
    void addDiscountToCart_whenDiscountNotFound_throwsNotFoundExceptionTest() {
        // Given
        Long cartId = cartDto.getId();
        String discountCode = "DISCOUNT1";
        given(discountJpaAdapter.existsById(discountCode)).willReturn(false);

        // When
        assertThatThrownBy(() -> cartRepository.addDiscountToCart(cartId, discountCode))
                .isInstanceOf(NotFoundException.class);

        // Then
        then(discountJpaAdapter).should().existsById(discountCode);
    }

    @Test
    void removeDiscountFromCartTest() {
        // Given
        Long cartId = cartDto.getId();
        String discountCode = "DISCOUNT1";
        given(cartJpaAdapter.findByIdFetchDiscountsAndProductIds(cartId)).willReturn(Optional.of(cartEntity));
        given(discountJpaAdapter.existsById(discountCode)).willReturn(true);
        given(cartEntityMapper.toDto(cartEntity)).willReturn(cartDto);

        // When
        CartDto result = cartRepository.removeDiscountFromCart(cartId, discountCode);

        // Then
        then(cartJpaAdapter).should().findByIdFetchDiscountsAndProductIds(cartId);
        then(discountJpaAdapter).should().existsById(discountCode);
        then(cartJpaAdapter).should().flush();
        then(cartEntityMapper).should().toDto(cartEntity);
        assertThat(result).isEqualTo(cartDto);
    }

    @Test
    void removeDiscountFromCart_whenDiscountNotFound_throwsNotFoundExceptionTest() {
        // Given
        Long cartId = cartDto.getId();
        String discountCode = "DISCOUNT1";
        given(cartJpaAdapter.findByIdFetchDiscountsAndProductIds(cartId)).willReturn(Optional.of(cartEntity));
        given(discountJpaAdapter.existsById(discountCode)).willReturn(false);

        // When
        assertThatThrownBy(() -> cartRepository.removeDiscountFromCart(cartId, discountCode))
                .isInstanceOf(NotFoundException.class);

        // Then
        then(cartJpaAdapter).should().findByIdFetchDiscountsAndProductIds(cartId);
        then(discountJpaAdapter).should().existsById(discountCode);
    }

    @Test
    void saveCartTest() {
        // Given
        given(cartEntityMapper.fromDto(cartDto)).willReturn(cartEntity);
        cartDto.getProducts().forEach(product -> given(productJpaAdapter.existsById(product.getProductId())).willReturn(true));
        cartDto.getDiscounts().forEach(discount -> given(discountJpaAdapter.existsById(discount)).willReturn(true));
        given(cartJpaAdapter.save(cartEntity)).willReturn(cartEntity);
        given(cartEntityMapper.toDto(cartEntity)).willReturn(cartDto);

        // When
        CartDto result = cartRepository.saveCart(cartDto);

        // Then
        cartDto.getProducts().forEach(product -> then(productJpaAdapter).should().existsById(product.getProductId()));
        cartDto.getDiscounts().forEach(discount -> then(discountJpaAdapter).should().existsById(discount));
        then(cartEntityMapper).should().fromDto(cartDto);
        then(cartJpaAdapter).should().save(cartEntity);
        then(cartJpaAdapter).should().flush();
        then(cartEntityMapper).should().toDto(cartEntity);
        assertThat(result).isEqualTo(cartDto);
    }

    @Test
    void updateCartTest() {
        CartEntity newCartEntity = ModelUtils.getCartEntity();
        ProductItemEntity productItem = ProductItemEntity.builder()
                .id(new ProductItemId(1L, 2L))
                .cart(cartEntity)
                .quantity(1)
                .build();
        newCartEntity.addProduct(productItem);
        // Given
        given(cartEntityMapper.fromDto(cartDto)).willReturn(newCartEntity);
        given(productJpaAdapter.existsById(productItem.getId().getProductId())).willReturn(true);
        given(cartJpaAdapter.findByIdFetchDiscountsAndProductIds(cartEntity.getId())).willReturn(Optional.of(cartEntity));
        given(cartEntityMapper.toDto(newCartEntity)).willReturn(cartDto);

        CartDto result = cartRepository.updateCart(cartDto);

        then(cartEntityMapper).should().fromDto(cartDto);
        then(productJpaAdapter).should().existsById(productItem.getId().getProductId());
        then(cartJpaAdapter).should().findByIdFetchDiscountsAndProductIds(cartEntity.getId());
        then(cartEntityMapper).should().toDto(newCartEntity);
        assertThat(result).isEqualTo(cartDto);
    }

    @Test
    void deleteCartTest() {
        // Given
        Long cartId = cartDto.getId();
        given(cartJpaAdapter.findByIdFetchDiscountsAndProductIds(cartId)).willReturn(Optional.of(cartEntity));
        given(cartEntityMapper.toDto(cartEntity)).willReturn(cartDto);

        // When
        CartDto result = cartRepository.deleteCart(cartId);

        // Then
        then(cartJpaAdapter).should().findByIdFetchDiscountsAndProductIds(cartId);
        then(cartJpaAdapter).should().deleteById(cartId);
        then(cartJpaAdapter).should().flush();
        assertThat(result).isEqualTo(cartDto);
    }

    @Test
    void getCartDtoByIdTest() {
        // Given
        Long cartId = cartDto.getId();
        given(cartJpaAdapter.findByIdFetchDiscountsAndProductIds(cartId)).willReturn(Optional.of(cartEntity));
        given(cartEntityMapper.toDto(cartEntity)).willReturn(cartDto);

        // When
        CartDto result = cartRepository.getCartDtoById(cartId);

        // Then
        then(cartJpaAdapter).should().findByIdFetchDiscountsAndProductIds(cartId);
        then(cartEntityMapper).should().toDto(cartEntity);
        assertThat(result).isEqualTo(cartDto);
    }
}


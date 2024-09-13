package org.example.dao.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.adapters.CartJpaAdapter;
import org.example.dao.entity.CartEntity;
import org.example.dao.mapper.CartEntityMapper;
import org.example.domain.dto.CartDto;
import org.example.domain.exception.NotFoundException;
import org.example.domain.repository.CartRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {
    private final CartJpaAdapter cartJpaAdapter;
    private final CartEntityMapper cartEntityMapper;

    // TODO: on update there are 2 read queries. Create different methods of adding products and discounts
    @Override
    public CartDto saveCart(CartDto cartDto) {
        CartEntity cartEntity = cartEntityMapper.fromDto(cartDto);
        cartEntity.getProducts().forEach(product -> product.setCart(cartEntity));
        CartEntity savedCartEntity= cartJpaAdapter.save(cartEntity);

        return getCartById(savedCartEntity.getId());
    }

    @Override
    public CartDto deleteCart(Long cartId) {
        CartDto cartDto = getCartById(cartId);

        cartJpaAdapter.deleteById(cartId);
        return cartDto;
    }

    @Override
    public CartDto getCartById(Long cartId) {
        return cartJpaAdapter.findByIdFetchDiscountsAndProductIds(cartId)
                .map(cartEntityMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Cart not found by id %s".formatted(cartId)));
    }
}

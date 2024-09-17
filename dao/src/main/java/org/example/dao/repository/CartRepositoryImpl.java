package org.example.dao.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.adapters.CartJpaAdapter;
import org.example.dao.adapters.DiscountJpaAdapter;
import org.example.dao.adapters.ProductJpaAdapter;
import org.example.dao.entity.CartEntity;
import org.example.dao.entity.ProductItemEntity;
import org.example.dao.mapper.CartEntityMapper;
import org.example.dao.mapper.ProductItemEntityMapper;
import org.example.domain.dto.CartDto;
import org.example.domain.dto.ProductItemDto;
import org.example.domain.exception.NotFoundException;
import org.example.domain.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {
    private final CartJpaAdapter cartJpaAdapter;
    private final ProductJpaAdapter productJpaAdapter;
    private final DiscountJpaAdapter discountJpaAdapter;
    private final CartEntityMapper cartEntityMapper;
    private final ProductItemEntityMapper productItemEntityMapper;

    @Override
    public CartDto addProductToCart(Long cartId, ProductItemDto productItemDto) {
        validateIfProductExistsById(productItemDto.getProductId());

        CartEntity cart = getCartById(cartId);

        Optional<ProductItemEntity> optionalProductItem = cart.getProducts().stream()
                .filter(product -> product.getId().getProductId().equals(productItemDto.getProductId()))
                .findFirst();

        if (optionalProductItem.isPresent()) {
            // Update product quantity
            optionalProductItem.get().setQuantity(productItemDto.getQuantity());
            log.info("Updated product item quantity {}", productItemDto);
        } else {
            // Add new product
            ProductItemEntity productItem = productItemEntityMapper.fromDto(productItemDto);
            cart.addProduct(productItem);
            log.info("Added product item {}", productItem);
        }

        cartJpaAdapter.flush();

        return cartEntityMapper.toDto(cart);
    }

    @Override
    public CartDto removeProductFromCart(Long cartId, Long productId) {
        validateIfProductExistsById(productId);

        CartEntity cart = getCartById(cartId);
        ProductItemEntity productItem = cart.getProducts().stream()
                .filter(product -> product.getId().getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Product with id: %s not found in a cart with id: %s".formatted(productId, cartId)));
        cart.removeProduct(productItem);
        cartJpaAdapter.flush();
        return cartEntityMapper.toDto(cart);
    }

    @Override
    public CartDto addDiscountToCart(Long cartId, String discountCode) {
        validateDiscountExistsByCode(discountCode);

        CartEntity cart = getCartById(cartId);

        cart.addDiscount(discountCode);

        cartJpaAdapter.flush();
        log.info("Added discount code {} to the cart {}", discountCode, cartId);
        return cartEntityMapper.toDto(cart);
    }

    @Override
    public CartDto removeDiscountFromCart(Long cartId, String code) {
        CartEntity cart = getCartById(cartId);
        validateDiscountExistsByCode(code);
        cart.removeDiscount(code);

        cartJpaAdapter.flush();
        log.info("Removed discount code {} from the cart {}", code, cartId);
        return cartEntityMapper.toDto(cart);
    }

    @Override
    public CartDto saveCart(CartDto cartDto) {
        cartDto.getProducts().forEach(product -> validateIfProductExistsById(product.getProductId()));
        cartDto.getDiscounts().forEach(this::validateDiscountExistsByCode);

        CartEntity cartEntity = cartEntityMapper.fromDto(cartDto);
        cartEntity.getProducts().forEach(product -> product.setCart(cartEntity));

        CartEntity savedCartEntity = cartJpaAdapter.save(cartEntity);
        cartJpaAdapter.flush();
        log.info("Saved cart entity: {}", savedCartEntity);
        return cartEntityMapper.toDto(savedCartEntity);
    }

    @Override
    public CartDto updateCart(CartDto cartDto) {
        CartEntity cartEntity = cartEntityMapper.fromDto(cartDto);
        CartEntity existingCart = getCartById(cartEntity.getId());

        existingCart.getDiscounts().removeIf(discount -> !cartEntity.getDiscounts().contains(discount));
        cartEntity.getDiscounts().removeIf(discount -> existingCart.getDiscounts().contains(discount));
        cartEntity.getDiscounts().forEach(this::validateDiscountExistsByCode);
        existingCart.getDiscounts().addAll(cartEntity.getDiscounts());

        updateProducts(existingCart, cartEntity.getProducts());

        cartJpaAdapter.flush();
        log.info("Updated cart entity: {}", cartEntity);
        return cartEntityMapper.toDto(existingCart);
    }

    @Override
    public CartDto deleteCart(Long cartId) {
        CartDto cartDto = getCartDtoById(cartId);

        cartJpaAdapter.deleteById(cartId);

        cartJpaAdapter.flush();
        log.info("Deleted cart entity: {}", cartDto);
        return cartDto;
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getCartDtoById(Long cartId) {
        return cartEntityMapper.toDto(getCartById(cartId));
    }

    private CartEntity getCartById(Long cartId) {
        return cartJpaAdapter.findByIdFetchDiscountsAndProductIds(cartId)
                .orElseThrow(() -> new NotFoundException("Cart with id: %s is not found".formatted(cartId)));
    }

    private void updateProducts(CartEntity existingCart, Set<ProductItemEntity> newProducts) {
        Set<ProductItemEntity> existingProducts = existingCart.getProducts();

        Map<Long, ProductItemEntity> newProductMap = newProducts.stream()
                .collect(Collectors.toMap(p -> p.getId().getProductId(), p -> p));

        Iterator<ProductItemEntity> iterator = existingProducts.iterator();
        while (iterator.hasNext()) {
            ProductItemEntity existingProduct = iterator.next();
            Long productId = existingProduct.getId().getProductId();

            if (newProductMap.containsKey(productId)) {
                ProductItemEntity newProduct = newProductMap.get(productId);
                existingProduct.setQuantity(newProduct.getQuantity());
                newProductMap.remove(productId);
            } else {
                iterator.remove();
            }
        }

        newProductMap.values().forEach(newProduct -> {
            validateIfProductExistsById(newProduct.getId().getProductId());
            newProduct.setCart(existingCart);
            existingCart.getProducts().add(newProduct);
        });
    }

    private void validateIfProductExistsById(Long productId) {
        if (!productJpaAdapter.existsById(productId)) {
            throw new NotFoundException("Product with id: %s is not found".formatted(productId));
        }
    }

    private void validateDiscountExistsByCode(String discountCode) {
        if (!discountJpaAdapter.existsById(discountCode)) {
            throw new NotFoundException("Discount with code: %s is not found".formatted(discountCode));
        }
    }
}

package org.example.dao.adapter;

import org.example.dao.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartJpaAdapter extends JpaRepository<CartEntity, Long> {
    @Query("SELECT c FROM CartEntity c LEFT JOIN FETCH c.discounts d WHERE c.id=:id")
    Optional<CartEntity> findByIdFetchDiscounts(Long id);
    @Query("SELECT c FROM CartEntity c LEFT JOIN FETCH c.products p WHERE c.id=:id")
    Optional<CartEntity> findByIdFetchProducts(Long id);
}

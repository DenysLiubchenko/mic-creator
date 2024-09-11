package org.example.dao.repository;

import org.example.dao.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    @Query("SELECT c FROM CartEntity c LEFT JOIN FETCH c.discounts d LEFT JOIN FETCH c.products p")
    Optional<CartEntity> findByIdFetchDiscountsAndProductIds(Long id);
}

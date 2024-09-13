package org.example.dao.adapters;

import org.example.dao.entity.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountJpaAdapter extends JpaRepository<DiscountEntity, String> {
}

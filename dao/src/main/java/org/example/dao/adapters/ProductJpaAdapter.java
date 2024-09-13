package org.example.dao.adapters;

import org.example.dao.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaAdapter extends JpaRepository<ProductEntity, Long> {
}

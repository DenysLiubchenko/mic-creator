package org.example.producer.adapter;

import org.example.producer.entity.OutBoxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutBoxRepository extends JpaRepository<OutBoxEntity, Long> {
}

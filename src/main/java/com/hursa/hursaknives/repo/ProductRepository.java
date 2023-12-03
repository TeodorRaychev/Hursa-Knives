package com.hursa.hursaknives.repo;

import com.hursa.hursaknives.model.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {}

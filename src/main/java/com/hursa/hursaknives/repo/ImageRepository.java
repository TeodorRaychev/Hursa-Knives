package com.hursa.hursaknives.repo;

import com.hursa.hursaknives.model.entity.ImageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

  List<ImageEntity> findByProductEntity_Id(Long id);
}

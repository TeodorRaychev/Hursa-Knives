package com.hursa.hursaknives.repo;

import com.hursa.hursaknives.model.entity.CloudinaryIds;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudinaryIdsRepository extends JpaRepository<CloudinaryIds, Long> {
  Optional<CloudinaryIds> findByPublicId(String publicId);
}

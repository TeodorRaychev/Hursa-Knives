package com.hursa.hursaknives.repo;

import com.hursa.hursaknives.model.entity.UserRoleEntity;
import com.hursa.hursaknives.model.enums.UserRoleEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {

  Optional<UserRoleEntity> findByRole(UserRoleEnum role);
}

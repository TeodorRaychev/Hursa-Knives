package com.hursa.hursaknives.repo;

import com.hursa.hursaknives.model.entity.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<ContactEntity, Long> {}

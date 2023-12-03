package com.hursa.hursaknives.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ImageEntity extends BaseEntity {

  @Column(name = "url")
  private String url;

  @ManyToOne(optional = false)
  private ProductEntity productEntity;
}

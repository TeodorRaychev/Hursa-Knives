package com.hursa.hursaknives.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ImageEntity extends BaseEntity {

  @Column(name = "url")
  private String url;

  @Column(name = "public_id")
  private String publicId;

  @ManyToOne(optional = false)
  private ProductEntity productEntity;
}

package com.hursa.hursaknives.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "products")
public class ProductEntity extends BaseEntity {
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @OneToMany(mappedBy = "productEntity", orphanRemoval = true)
  private List<ImageEntity> images;

  @Column(name = "price")
  private BigDecimal price;

  @Column(name = "material")
  private String material;
}

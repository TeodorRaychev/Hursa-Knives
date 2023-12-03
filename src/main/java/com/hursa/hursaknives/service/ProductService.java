package com.hursa.hursaknives.service;

import com.hursa.hursaknives.model.dto.ProductBindingModel;
import com.hursa.hursaknives.model.dto.ProductViewDTO;
import com.hursa.hursaknives.model.entity.ImageEntity;
import com.hursa.hursaknives.model.entity.ProductEntity;
import com.hursa.hursaknives.repo.ImageRepository;
import com.hursa.hursaknives.repo.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final ModelMapper modelMapper;
  private final ImageRepository imageRepository;

  public ProductService(
      ProductRepository productRepository,
      ModelMapper modelMapper,
      ImageRepository imageRepository) {
    this.productRepository = productRepository;
    this.modelMapper = modelMapper;
    this.imageRepository = imageRepository;
  }

  public ProductEntity addProduct(ProductBindingModel productBindingModel) {
    if (productBindingModel.name() == null) {
      throw new IllegalArgumentException("Product name cannot be null");
    }
    return productRepository.saveAndFlush(
        modelMapper.map(productBindingModel, ProductEntity.class));
  }

  public List<ProductViewDTO> getAllProducts() {
    return productRepository.findAll().stream().map(this::map).toList();
  }

  private ProductViewDTO map(ProductEntity e) {
    List<ImageEntity> images = imageRepository.findByProductEntity_Id(e.getId());
    return new ProductViewDTO(
        e.getId(),
        e.getName(),
        e.getDescription(),
        e.getPrice() != null ? e.getPrice().doubleValue() : null,
        e.getMaterial(),
        images.stream().map(ImageEntity::getUrl).collect(Collectors.toList()));
  }

  public void deleteProduct(Long id) {
    productRepository.deleteById(id);
  }

  public ProductViewDTO getProductById(Long id) {
    ProductEntity productEntity =
        productRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    return map(productEntity);
  }
}

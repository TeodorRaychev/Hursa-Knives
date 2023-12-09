package com.hursa.hursaknives.service;

import com.hursa.hursaknives.model.dto.ImageDTO;
import com.hursa.hursaknives.model.dto.ProductBindingModel;
import com.hursa.hursaknives.model.dto.ProductViewDTO;
import com.hursa.hursaknives.model.entity.ImageEntity;
import com.hursa.hursaknives.model.entity.ProductEntity;
import com.hursa.hursaknives.repo.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final ModelMapper modelMapper;
  private final ImageService imageService;

  public ProductService(
      ProductRepository productRepository, ModelMapper modelMapper, ImageService imageService) {
    this.productRepository = productRepository;
    this.modelMapper = modelMapper;
    this.imageService = imageService;
  }

  public ProductEntity addProduct(ProductBindingModel productBindingModel) {
    if (productBindingModel.name() == null) {
      throw new IllegalArgumentException("Product name cannot be null");
    }
    return productRepository.saveAndFlush(
        modelMapper.map(productBindingModel, ProductEntity.class));
  }

  @Transactional
  public List<ProductViewDTO> getAllProducts() {
    return productRepository.findAll().stream().map(this::map).toList();
  }

  private ProductViewDTO map(ProductEntity e) {
    List<ImageDTO> images = getImagesForProduct(e);
    return new ProductViewDTO(
        e.getId(),
        e.getName(),
        e.getDescription(),
        e.getPrice() != null ? e.getPrice().doubleValue() : null,
        e.getMaterial(),
        images);
  }

  @Transactional
  public void deleteProduct(Long id) {
    ProductEntity product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    for (ImageEntity image : product.getImages()) {
      imageService.delete(image.getId());
    }
    productRepository.deleteById(id);
  }

  @Transactional
  public ProductViewDTO getProductViewDTOById(Long id) {
    ProductEntity productEntity =
        productRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    return map(productEntity);
  }

  public List<ImageDTO> getImagesForProduct(ProductEntity productEntity) {
    return productEntity.getImages().stream()
        .map(i -> new ImageDTO(i.getId(), i.getUrl()))
        .collect(Collectors.toList());
  }

  public ProductEntity updateProduct(Long id, ProductBindingModel productBindingModel) {
    if (productBindingModel.name() == null) {
      throw new IllegalArgumentException("Product name cannot be null");
    }
    if (productBindingModel.description() == null) {
      throw new IllegalArgumentException("Product description cannot be null");
    }
    ProductEntity productEntity =
        productRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    boolean isChanged = false;
    if (!productBindingModel.name().equals(productEntity.getName())) {
      productEntity.setName(productBindingModel.name());
      isChanged = true;
    }
    if (!productBindingModel.description().equals(productEntity.getDescription())) {
      productEntity.setDescription(productBindingModel.description());
      isChanged = true;
    }
    if (productBindingModel.price() != null && productEntity.getPrice() == null
        || productBindingModel.price() != null
            && !productBindingModel.price().equals(productEntity.getPrice().doubleValue())) {
      productEntity.setPrice(BigDecimal.valueOf(productBindingModel.price()));
      isChanged = true;
    }
    if (productBindingModel.material() != null && productEntity.getMaterial() == null
        || productBindingModel.material() != null
            && !productBindingModel.material().equals(productEntity.getMaterial())) {
      productEntity.setMaterial(productBindingModel.material());
      isChanged = true;
    }
    if (isChanged) {
      productRepository.saveAndFlush(productEntity);
    }
    return productEntity;
  }
}

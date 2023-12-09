package com.hursa.hursaknives.service;

import com.hursa.hursaknives.model.dto.ImageBindingModel;
import com.hursa.hursaknives.model.entity.ImageEntity;
import com.hursa.hursaknives.model.entity.ProductEntity;
import com.hursa.hursaknives.repo.ImageRepository;
import com.hursa.hursaknives.repo.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageService {
  private final ImageRepository imageRepository;
  private final ProductRepository productRepository;
  private final CloudinaryIdsService cloudinaryIdsService;

  public ImageService(
      ImageRepository imageRepository,
      ProductRepository productRepository,
      CloudinaryIdsService cloudinaryIdsService) {
    this.imageRepository = imageRepository;
    this.productRepository = productRepository;
    this.cloudinaryIdsService = cloudinaryIdsService;
  }

  public String saveImage(ImageBindingModel imageBindingModel) {
    if (imageBindingModel.url() == null) {
      throw new IllegalArgumentException("Url cannot be null");
    }
    if (imageBindingModel.productId() == null) {
      throw new IllegalArgumentException("Product id cannot be null");
    }
    ProductEntity productEntity =
        productRepository
            .findById(imageBindingModel.productId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Product with id " + imageBindingModel.productId() + " not found"));
    ImageEntity imageEntity = new ImageEntity();
    imageEntity.setUrl(imageBindingModel.url());
    imageEntity.setPublicId(imageBindingModel.publicId());
    imageEntity.setProductEntity(productEntity);
    imageRepository.saveAndFlush(imageEntity);
    return imageEntity.getUrl();
  }

  @Transactional
  public void delete(Long id) {
    ImageEntity imageEntity =
        imageRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Image with id " + id + " not found"));
    imageRepository.deleteById(id);
    if (imageRepository.findById(id).isEmpty()) {
      cloudinaryIdsService.save(imageEntity.getPublicId());
    }
  }
}

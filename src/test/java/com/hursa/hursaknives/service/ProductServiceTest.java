package com.hursa.hursaknives.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hursa.hursaknives.model.dto.ImageDTO;
import com.hursa.hursaknives.model.dto.ProductBindingModel;
import com.hursa.hursaknives.model.dto.ProductViewDTO;
import com.hursa.hursaknives.model.entity.ImageEntity;
import com.hursa.hursaknives.model.entity.ProductEntity;
import com.hursa.hursaknives.repo.ProductRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.record.RecordModule;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
  private final ModelMapper modelMapper = new ModelMapper().registerModule(new RecordModule());
  @Mock private ProductRepository productRepository;
  private ProductService serviceToTest;
  private List<ProductEntity> products;

  @BeforeEach
  void setup() {
    serviceToTest = new ProductService(productRepository, modelMapper);
    List<ImageEntity> imageEntityList =
        List.of(
            (ImageEntity) new ImageEntity().setUrl("testUrl").setId(1L),
            (ImageEntity) new ImageEntity().setUrl("testUrl2").setId(2L),
            (ImageEntity) new ImageEntity().setUrl("testUrl3").setId(3L));
    products =
        List.of(
            (ProductEntity)
                new ProductEntity()
                    .setName("testName")
                    .setDescription("testDescription")
                    .setPrice(BigDecimal.valueOf(1.0))
                    .setMaterial("testMaterial")
                    .setImages(new ArrayList<>())
                    .setId(1L),
            (ProductEntity)
                new ProductEntity()
                    .setName("testName1")
                    .setDescription("testDescription1")
                    .setPrice(BigDecimal.valueOf(2.0))
                    .setImages(List.of(imageEntityList.get(0)))
                    .setId(2L),
            (ProductEntity)
                new ProductEntity()
                    .setName("testName2")
                    .setDescription("testDescription2")
                    .setMaterial("testMaterial2")
                    .setImages(List.of(imageEntityList.get(1), imageEntityList.get(2)))
                    .setId(3L),
            (ProductEntity)
                new ProductEntity()
                    .setName("testName3")
                    .setDescription("testDescription3")
                    .setImages(new ArrayList<>())
                    .setId(4L));
  }

  @Test
  void addProduct() {
    String name = "testName";
    String description = "testDescription";
    Double price = 1.0;
    String material = "testMaterial";
    ProductBindingModel productBindingModel =
        new ProductBindingModel(name, description, price, material);
    when(productRepository.saveAndFlush(any(ProductEntity.class)))
        .thenReturn(modelMapper.map(productBindingModel, ProductEntity.class));

    ProductEntity productEntity = serviceToTest.addProduct(productBindingModel);

    assertEquals(name, productEntity.getName());
    assertEquals(description, productEntity.getDescription());
    assertEquals(price, productEntity.getPrice().doubleValue());
    assertEquals(material, productEntity.getMaterial());
  }

  @Test
  void addProductThrowsWhenNameIsNull() {
    ProductBindingModel productBindingModel = new ProductBindingModel(null, null, null, null);
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.addProduct(productBindingModel),
            "Should throw when name is null");
    assertEquals("Product name cannot be null", exception.getMessage());
  }

  @Test
  void getAllProducts() {
    when(productRepository.findAll()).thenReturn(products);
    List<ProductViewDTO> allProducts = serviceToTest.getAllProducts();

    assertEquals(4, allProducts.size());
    assertEquals("testName", allProducts.get(0).name());
    assertEquals("testName1", allProducts.get(1).name());
    assertEquals("testName2", allProducts.get(2).name());
    assertEquals("testName3", allProducts.get(3).name());

    assertEquals("testDescription", allProducts.get(0).description());
    assertEquals("testDescription1", allProducts.get(1).description());
    assertEquals("testDescription2", allProducts.get(2).description());
    assertEquals("testDescription3", allProducts.get(3).description());

    assertEquals(1.0, allProducts.get(0).price());
    assertEquals(2.0, allProducts.get(1).price());
    assertNull(allProducts.get(2).price());
    assertNull(allProducts.get(3).price());

    assertEquals("testMaterial", allProducts.get(0).material());
    assertEquals("testMaterial2", allProducts.get(2).material());
    assertNull(allProducts.get(1).material());
    assertNull(allProducts.get(3).material());

    assertEquals(1L, allProducts.get(0).id());
    assertEquals(2L, allProducts.get(1).id());
    assertEquals(3L, allProducts.get(2).id());
    assertEquals(4L, allProducts.get(3).id());

    assertEquals(0, allProducts.get(0).images().size());
    assertEquals(1, allProducts.get(1).images().size());
    assertEquals(2, allProducts.get(2).images().size());
    assertEquals(0, allProducts.get(3).images().size());

    assertEquals("testUrl", allProducts.get(1).images().get(0).url());
    assertEquals("testUrl2", allProducts.get(2).images().get(0).url());
    assertEquals("testUrl3", allProducts.get(2).images().get(1).url());

    assertEquals(1L, allProducts.get(1).images().get(0).id());
    assertEquals(2L, allProducts.get(2).images().get(0).id());
    assertEquals(3L, allProducts.get(2).images().get(1).id());
  }

  @Test
  void getProductViewDTOById() {
    when(productRepository.findById(2L)).thenReturn(Optional.of(products.get(1)));
    ProductViewDTO productViewDTO = serviceToTest.getProductViewDTOById(2L);

    assertEquals("testName1", productViewDTO.name());
    assertEquals("testDescription1", productViewDTO.description());
    assertEquals(2.0, productViewDTO.price());
    assertNull(productViewDTO.material());
    assertEquals(2L, productViewDTO.id());
    assertEquals(1, productViewDTO.images().size());
    assertEquals("testUrl", productViewDTO.images().get(0).url());
    assertEquals(1L, productViewDTO.images().get(0).id());

    when(productRepository.findById(1L)).thenReturn(Optional.of(products.get(0)));
    productViewDTO = serviceToTest.getProductViewDTOById(1L);

    assertEquals("testMaterial", productViewDTO.material());
  }

  @Test
  void getProductViewDTOByIdThrowsWhenProductNotFound() {
    when(productRepository.findById(1L)).thenReturn(Optional.empty());
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> serviceToTest.getProductViewDTOById(1L));

    assertEquals("Product not found", exception.getMessage());
  }

  @Test
  void getImagesForProduct() {
    List<ImageDTO> imagesForProduct = serviceToTest.getImagesForProduct(products.get(2));

    assertEquals(2, imagesForProduct.size());
    assertEquals("testUrl2", imagesForProduct.get(0).url());
    assertEquals("testUrl3", imagesForProduct.get(1).url());

    assertEquals(2L, imagesForProduct.get(0).id());
    assertEquals(3L, imagesForProduct.get(1).id());
  }

  @Test
  void updateProduct() {
    String name = "testName";
    String description = "testDescription";
    Double price = 1.0;
    String material = "testMaterial";
    ProductBindingModel productBindingModel =
        new ProductBindingModel(name, description, price, material);
    when(productRepository.findById(1L)).thenReturn(Optional.of(products.get(2)));
    when(productRepository.saveAndFlush(any(ProductEntity.class)))
        .thenReturn(modelMapper.map(productBindingModel, ProductEntity.class));

    ProductEntity productEntity = serviceToTest.updateProduct(1L, productBindingModel);

    assertEquals(name, productEntity.getName());
    assertEquals(description, productEntity.getDescription());
    assertEquals(price, productEntity.getPrice().doubleValue());
    assertEquals(material, productEntity.getMaterial());

    when(productRepository.findById(2L)).thenReturn(Optional.of(products.get(1)));
    when(productRepository.saveAndFlush(any(ProductEntity.class)))
        .thenReturn(modelMapper.map(productBindingModel, ProductEntity.class));
    productEntity = serviceToTest.updateProduct(2L, productBindingModel);

    assertEquals(name, productEntity.getName());
    assertEquals(description, productEntity.getDescription());
    assertEquals(price, productEntity.getPrice().doubleValue());
    assertEquals(material, productEntity.getMaterial());
  }

  @Test
  void updateProductThrowsWhenProductNotFound() {
    String name = "testName";
    String description = "testDescription";
    Double price = 1.0;
    String material = "testMaterial";
    ProductBindingModel productBindingModel =
        new ProductBindingModel(name, description, price, material);
    when(productRepository.findById(1L)).thenReturn(Optional.empty());
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.updateProduct(1L, productBindingModel));
    assertEquals("Product not found", exception.getMessage());
  }

  @Test
  void updateProductThrowsWhenNameIsNull() {
    ProductBindingModel productBindingModel = new ProductBindingModel(null, null, null, null);
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.updateProduct(1L, productBindingModel),
            "Should throw when name is null");
    assertEquals("Product name cannot be null", exception.getMessage());
  }

  @Test
  void updateProductThrowsWhenDescriptionIsNull() {
    ProductBindingModel productBindingModel = new ProductBindingModel("name", null, null, null);
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.updateProduct(1L, productBindingModel),
            "Should throw when description is null");
    assertEquals("Product description cannot be null", exception.getMessage());
  }
}

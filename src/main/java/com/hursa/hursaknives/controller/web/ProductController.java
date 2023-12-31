package com.hursa.hursaknives.controller.web;

import com.hursa.hursaknives.model.dto.ImageBindingModel;
import com.hursa.hursaknives.model.dto.ProductBindingModel;
import com.hursa.hursaknives.model.dto.ProductViewDTO;
import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.model.entity.ProductEntity;
import com.hursa.hursaknives.service.FileUploadService;
import com.hursa.hursaknives.service.ImageService;
import com.hursa.hursaknives.service.ProductService;
import com.hursa.hursaknives.service.UserService;
import jakarta.validation.Valid;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {
  private final UserService userService;
  private final ProductService productService;
  private final ImageService imageService;
  private final FileUploadService fileUploadService;
  private final Logger logger = Logger.getLogger(ProductController.class.getName());

  public ProductController(
      UserService userService,
      ProductService productService,
      ImageService imageService,
      FileUploadService fileUploadService) {
    this.userService = userService;
    this.productService = productService;
    this.imageService = imageService;
    this.fileUploadService = fileUploadService;
  }

  @ModelAttribute("userProfile")
  public ProfileBindingModel getUserProfile() {
    return userService
        .getUserProfile(SecurityContextHolder.getContext().getAuthentication().getName())
        .orElse(null);
  }

  @ModelAttribute("products")
  public List<ProductViewDTO> getProducts() {
    return productService.getAllProducts();
  }

  @ModelAttribute("errorMessage")
  public String getErrorMessage() {
    return null;
  }

  @GetMapping
  public String products() {
    return "products";
  }

  @GetMapping("/admin/add")
  public String addGet(Model model) {
    if (!model.containsAttribute("productBindingModel")) {
      model.addAttribute("productBindingModel", new ProductBindingModel(null, null, null, null));
    }
    return "productAdd";
  }

  @PostMapping("/admin/add")
  public String addPost(
      @RequestParam List<MultipartFile> images,
      @Valid ProductBindingModel productBindingModel,
      BindingResult bindingResult,
      RedirectAttributes rAtt) {

    if (bindingResult.hasErrors()) {
      rAtt.addFlashAttribute("productBindingModel", productBindingModel);
      rAtt.addFlashAttribute(
          "org.springframework.validation.BindingResult.productBindingModel", bindingResult);
      rAtt.addFlashAttribute("errorMessage", "Unable to add product");
      return "redirect:/products/admin/add";
    }

    ProductEntity productEntity = productService.addProduct(productBindingModel);
    addImage(images, productEntity.getId());
    return "redirect:/products";
  }

  @DeleteMapping("/admin/delete/{id}")
  public String delete(@PathVariable Long id, RedirectAttributes rAtt) {
    productService.deleteProduct(id);
    rAtt.addFlashAttribute("successMessage", "Product deleted successfully");
    return "redirect:/products";
  }

  @GetMapping("/details/{id}")
  public String details(@PathVariable Long id, Model model) {
    if (!model.containsAttribute("product")) {
      model.addAttribute("product", productService.getProductViewDTOById(id));
    }
    return "productDetails";
  }

  @GetMapping("/admin/edit/{id}")
  public String edit(@PathVariable Long id, Model model) {
    ProductViewDTO productViewDTOById = productService.getProductViewDTOById(id);
    ProductBindingModel productBindingModel =
        new ProductBindingModel(
            productViewDTOById.name(),
            productViewDTOById.description(),
            productViewDTOById.price(),
            productViewDTOById.material());
    model.addAttribute("productViewDTO", productViewDTOById);
    model.addAttribute("productBindingModel", productBindingModel);
    return "productEdit";
  }

  @PostMapping("/admin/edit/{id}")
  public String editPost(
      @PathVariable Long id,
      @RequestParam List<MultipartFile> images,
      @Valid ProductBindingModel productBindingModel,
      BindingResult bindingResult,
      RedirectAttributes rAtt) {

    if (bindingResult.hasErrors()) {
      rAtt.addFlashAttribute("productBindingModel", productBindingModel);
      rAtt.addFlashAttribute(
          "org.springframework.validation.BindingResult.productBindingModel", bindingResult);
      rAtt.addFlashAttribute("errorMessage", "Unable to edit product");
      return "redirect:/products/admin/edit/" + id;
    }

    productService.updateProduct(id, productBindingModel);
    addImage(images, id);
    return "redirect:/products/admin/edit/" + id;
  }

  @DeleteMapping("/admin/edit/{id}/images/delete/{imageId}")
  public String deleteImage(
      @PathVariable Long id, @PathVariable Long imageId, RedirectAttributes rAtt) {
    imageService.delete(imageId);
    rAtt.addFlashAttribute("successMessage", "Image deleted successfully");
    return "redirect:/products/admin/edit/" + id;
  }

  private void addImage(List<MultipartFile> images, Long productId) {
    if (!images.isEmpty()) {
      for (MultipartFile image : images) {
        try (InputStream input = image.getInputStream()) {
          try {
            ImageIO.read(input).toString();
            // It's an images (only BMP, GIF, JPEG, PNG, TIFF and WBMP are recognized).
          } catch (Exception e) {
            continue;
          }
        } catch (Exception e) {
          continue;
        }
        String url = null;
        String publicId = null;
        try {
          Map<String, String> res = fileUploadService.uploadFile(image);
          url = res.get("url");
          publicId = res.get("public_id");
        } catch (Exception e) {
          logger.severe(e.getMessage());
        }
        imageService.saveImage(new ImageBindingModel(url, productId, publicId));
      }
    }
  }
}

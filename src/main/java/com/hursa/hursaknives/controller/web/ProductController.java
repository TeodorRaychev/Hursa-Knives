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
import javax.imageio.ImageIO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
  public String index() {
    return "products";
  }

  @GetMapping("/admin/add")
  public String add(Model model) {
    if (!model.containsAttribute("productBindingModel")) {
      model.addAttribute("productBindingModel", new ProductBindingModel(null, null, null, null));
    }
    return "productAdd";
  }

  @PostMapping("/admin/add")
  public String add(
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
        try {
          url = fileUploadService.uploadFile(image);
        } catch (Exception e) {
          e.printStackTrace();
        }
        imageService.saveImage(new ImageBindingModel(url, productEntity.getId()));
      }
    }
    return "redirect:/products";
  }

  @DeleteMapping("/admin/delete/{id}")
  @Transactional
  public String delete(@PathVariable Long id, RedirectAttributes rAtt) {
    productService.deleteProduct(id);
    rAtt.addFlashAttribute("successMessage", "Product deleted successfully");
    return "redirect:/products";
  }

  @GetMapping("/details/{id}")
  public String edit(@PathVariable Long id, Model model) {
    if (!model.containsAttribute("product")) {
      model.addAttribute("product", productService.getProductById(id));
    }
    return "productDetails";
  }
}

package com.hursa.hursaknives.controller.web;

import static org.hamcrest.Matchers.equalToObject;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.hursa.hursaknives.model.dto.ProductBindingModel;
import com.hursa.hursaknives.service.FileUploadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTestIT {
  private final MockMvc mockMvc;

  @Value("${hursa.admin.email}")
  private String adminEmail;

  @Value("${hursa.admin.password}")
  private String adminPassword;

  @MockBean private FileUploadService fileUploadService;

  @Autowired
  ProductControllerTestIT(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  void products() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/products")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().isOk())
        .andExpect(view().name("products"))
        .andExpect(model().attributeExists("products"))
        .andExpect(model().attributeExists("userProfile"))
        .andExpect(model().attribute("errorMessage", nullValue()));
  }

  @Test
  void addGet() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/products/admin/add")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().isOk())
        .andExpect(view().name("productAdd"))
        .andExpect(model().attributeExists("products"))
        .andExpect(model().attributeExists("userProfile"))
        .andExpect(model().attribute("errorMessage", nullValue()))
        .andExpect(model().attributeExists("productBindingModel"))
        .andExpect(
            model()
                .attribute(
                    "productBindingModel",
                    equalToObject(new ProductBindingModel(null, null, null, null))));
  }

  @Test
  void testAddPost() throws Exception {
    when(fileUploadService.uploadFile(any())).thenReturn(Map.of("uuid", "uuid", "url", "test"));
    MockMultipartFile file =
        new MockMultipartFile("images", "image.jpg", "file", "test".getBytes());
    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/products/admin/add")
                .file(file)
                .param("name", "test")
                .param("description", "test123")
                .param("price", "10")
                .param("images", "test")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/products"));
  }
}

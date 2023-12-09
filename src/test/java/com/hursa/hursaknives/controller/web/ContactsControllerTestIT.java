package com.hursa.hursaknives.controller.web;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
class ContactsControllerTestIT {
  private final MockMvc mockMvc;
  @MockBean private FileUploadService fileUploadService;

  @Value("${hursa.admin.email}")
  private String adminEmail;

  @Value("${hursa.admin.password}")
  private String adminPassword;

  @Autowired
  ContactsControllerTestIT(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  void getUserProfile() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/contacts")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().isOk())
        .andExpect(view().name("contacts"))
        .andExpect(model().attribute("userProfile", notNullValue()));
  }

  @Test
  void getAllContacts() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/contacts").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("contacts"))
        .andExpect(model().attribute("userProfile", nullValue()))
        .andExpect(model().attribute("contacts", notNullValue()));
  }

  @Test
  void addContact() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/contacts/admin/add")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().isOk())
        .andExpect(view().name("contactAdd"))
        .andExpect(model().attribute("userProfile", notNullValue()))
        .andExpect(model().attribute("contact", nullValue()));
  }

  @Test
  void testAddContact() throws Exception {
    when(fileUploadService.uploadFile(any())).thenReturn(Map.of("uuid", "uuid", "url", "test"));
    MockMultipartFile file = new MockMultipartFile("image", "image.jpg", "file", "test".getBytes());
    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/contacts/admin/add")
                .file(file)
                .param("firstName", "test")
                .param("email", "test@test.com")
                .param("lastName", "test")
                .param("additionalInfo", "test")
                .param("image", "test")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/contacts"));
  }

  @Test
  void editContact() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/contacts/admin/edit/1")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().isOk())
        .andExpect(view().name("contactEdit"))
        .andExpect(model().attribute("userProfile", notNullValue()))
        .andExpect(model().attribute("contactDTO", notNullValue()));
  }

  @Test
  void removeContactImage() {}

  @Test
  void testEditContact() throws Exception {
    when(fileUploadService.uploadFile(any())).thenReturn(Map.of("uuid", "uuid", "url", "test"));
    MockMultipartFile file = new MockMultipartFile("image", "image.jpg", "file", "test".getBytes());
    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/contacts/admin/edit/1")
                .file(file)
                .param("firstName", "test")
                .param("email", "test@test.com")
                .param("lastName", "test")
                .param("additionalInfo", "test")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/contacts/admin/edit/1"))
        .andExpect(flash().attribute("successMessage", "Successfully updated contact!"));
  }

  @Test
  void testEditContactWithInvalidData() throws Exception {
    when(fileUploadService.uploadFile(any())).thenReturn(Map.of("uuid", "uuid", "url", "test"));
    MockMultipartFile file = new MockMultipartFile("image", "image.jpg", "file", "test".getBytes());
    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/contacts/admin/edit/1")
                .file(file)
                .param("firstName", "t")
                .param("email", "test")
                .param("lastName", "1")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/contacts/admin/edit/1"))
        .andExpect(flash().attribute("errorMessage", "Unable to update contact!"))
        .andExpect(
            flash()
                .attribute(
                    "org.springframework.validation.BindingResult.contactDTO", notNullValue()));
  }

  @Test
  void removeContact() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/contacts/admin/delete/1")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/contacts"));
  }
}

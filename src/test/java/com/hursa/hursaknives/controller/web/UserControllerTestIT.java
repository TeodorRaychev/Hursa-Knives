package com.hursa.hursaknives.controller.web;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.hursa.hursaknives.model.dto.RegistrationBindingModel;
import com.hursa.hursaknives.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.DependsOn;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTestIT {
  private final MockMvc mockMvc;
  private final UserService userService;

  @Value("${hursa.admin.email}")
  private String adminEmail;

  @Value("${hursa.admin.password}")
  private String adminPassword;

  @Autowired
  UserControllerTestIT(MockMvc mockMvc, UserService userService) {
    this.mockMvc = mockMvc;
    this.userService = userService;
  }

  @Test
  void loginGet() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/login").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("login"));
  }

  @Test
  void loginPost() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/login")
                .param("email", adminEmail)
                .param("password", adminPassword)
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  void loginPostBadCredentials() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/login-error")
                .param("email", "not.valid@test.com")
                .param("password", "adminPassword")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("login"))
        .andExpect(model().attribute("errorMessage", "Invalid email or password"))
        .andExpect(model().attribute("email", "not.valid@test.com"))
        .andExpect(model().attribute("badCredentials", true))
        .andExpect(model().attribute("rememberMe", nullValue()));
  }

  @Test
  void registerGet() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/register").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("register"))
        .andExpect(model().attributeExists("registrationBindingModel"));
  }

  @Test
  void registerAdminGet() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/users/register/admin")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().isOk())
        .andExpect(view().name("register"))
        .andExpect(model().attributeExists("registrationBindingModel"));
  }

  @Test
  void registerPost() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/register")
                .param("email", "test@test.com")
                .param("password", "test123")
                .param("confirmPassword", "test123")
                .param("firstName", "test")
                .param("lastName", "test")
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/"));
  }

  @Test
  void registerPostWithInvalidData() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/register")
                .param("email", "test@test.com")
                .param("password", "t")
                .param("confirmPassword", "e")
                .param("firstName", "s")
                .param("lastName", "t")
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/users/register"))
        .andExpect(flash().attribute("errorMessage", "Unable to register"));
  }

  @Test
  void registerPostAdmin() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/register/admin")
                .param("email", "admin@test.com")
                .param("password", "test123")
                .param("confirmPassword", "test123")
                .param("firstName", "test")
                .param("lastName", "test")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/users/register/admin"))
        .andExpect(flash().attribute("successMessage", "User registered successfully"));
  }

  @Test
  void registerPostAdminWithInvalidData() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/register/admin")
                .param("email", "admin@test.com")
                .param("password", "t")
                .param("confirmPassword", "e")
                .param("firstName", "s")
                .param("lastName", "t")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/users/register/admin"))
        .andExpect(flash().attribute("errorMessage", "Unable to register"))
        .andExpect(flash().attributeExists("registrationBindingModel"))
        .andExpect(
            flash()
                .attributeExists(
                    "org.springframework.validation.BindingResult.registrationBindingModel"));
  }

  @Test
  void profileGetWithoutLogin() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/profile/1").with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/users/login"));
  }

  @Test
  void profile() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/users/profile/1")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().isOk())
        .andExpect(view().name("profile"))
        .andExpect(model().attributeExists("userProfile"));
  }

  @Test
  void loginError() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/login")
                .param("email", "not.valid@test.com")
                .param("password", "t")
                .param("rememberMe", "true")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(forwardedUrl("/users/login-error"));
  }

  @Test
  void getAllUsers() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/users/admin/edit")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().isOk())
        .andExpect(view().name("users"))
        .andExpect(model().attributeExists("users"));
  }

  @Test
  void editUser() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/users/admin/edit/1")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().isOk())
        .andExpect(view().name("profile"))
        .andExpect(model().attributeExists("userProfile"));
  }

  @Test
  @DependsOn(value = "registerPost()")
  void editProfileWithDuplicateEmail() throws Exception {
    if (userService.getUserProfile("test@test.com").isEmpty()) {
      userService.registerUser(
          new RegistrationBindingModel(
              "test@test.com", "test123", "test123", "test", "test", null));
    }
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/profile/1")
                .param("email", "test@test.com")
                .param("password", "test123")
                .param("confirmPassword", "test123")
                .param("firstName", "test")
                .param("lastName", "test")
                .param("oldPassword", adminPassword)
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().is3xxRedirection())
        .andExpect(flash().attributeExists("userProfile"))
        .andExpect(flash().attribute("errorMessage", "Unable to update profile"))
        .andExpect(view().name("redirect:/users/admin/edit/1"));
  }

  @Test
  void editProfile() throws Exception {
    if (userService.getAllUsers().size() < 2) {
      userService.registerUser(
          new RegistrationBindingModel(
              "test@test.com", "test123", "test123", "test", "test", null));
    }
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users/profile/2")
                .param("email", "test1234@test.com")
                .param("password", "test1234")
                .param("confirmPassword", "test1234")
                .param("firstName", "test")
                .param("lastName", "test")
                .param("oldPassword", "test123")
                .with(csrf())
                .with(user("test@test.com").password("test123").roles("USER")))
        .andExpect(status().is3xxRedirection())
        .andExpect(flash().attributeExists("userProfile"))
        .andExpect(flash().attribute("successMessage", "Profile updated successfully"))
        .andExpect(view().name("redirect:/users/profile/2"));
  }

  @Test
  void deleteUser() throws Exception {
    if (userService.getAllUsers().size() < 2) {
      userService.registerUser(
          new RegistrationBindingModel(
              "test@test.com", "test123", "test123", "test", "test", null));
    }
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/users/admin/delete/2")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/users/admin/edit"))
        .andExpect(flash().attribute("successMessage", "User deleted successfully"))
        .andExpect(view().name("redirect:/users/admin/edit"));
  }

  @Test
  void deleteSelf() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/users/admin/delete/1")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/users/admin/edit"))
        .andExpect(flash().attribute("errorMessage", "You cannot delete yourself"))
        .andExpect(view().name("redirect:/users/admin/edit"));
  }
}

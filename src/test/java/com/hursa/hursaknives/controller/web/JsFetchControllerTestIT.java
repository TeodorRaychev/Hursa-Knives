package com.hursa.hursaknives.controller.web;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class JsFetchControllerTestIT {
  private final MockMvc mockMvc;

  @Value("${hursa.admin.email}")
  private String adminEmail;

  @Value("${hursa.admin.password}")
  private String adminPassword;

  @Autowired
  JsFetchControllerTestIT(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  void getUserProfile() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/wiki")
                .with(csrf())
                .with(user(adminEmail).password(adminPassword).roles("ADMIN", "USER")))
        .andExpect(status().isOk())
        .andExpect(view().name("jsFetch"))
        .andExpect(model().attribute("userProfile", notNullValue()));
  }

  @Test
  void jsFetch() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/wiki").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("jsFetch"))
        .andExpect(model().attribute("userProfile", nullValue()));
  }
}

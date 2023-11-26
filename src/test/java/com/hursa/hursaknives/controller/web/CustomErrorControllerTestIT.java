package com.hursa.hursaknives.controller.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hursa.hursaknives.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", roles = "USER")
class CustomErrorControllerTestIT {
  private final MockMvc mockMvc;

  private final UserService userService;

  @Autowired
  CustomErrorControllerTestIT(MockMvc mockMvc, UserService userService) {
    this.mockMvc = mockMvc;
    this.userService = userService;
  }

  @Test
  void handleError404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/non-existing-url").with(csrf()))
        .andExpect(status().is(404));
  }
}

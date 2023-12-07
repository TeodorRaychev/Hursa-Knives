package com.hursa.hursaknives.controller.web;

import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class JsFetchController {
  private final UserService userService;

  public JsFetchController(UserService userService) {
    this.userService = userService;
  }

  @ModelAttribute("userProfile")
  public ProfileBindingModel getUserProfile() {
    return userService
        .getUserProfile(SecurityContextHolder.getContext().getAuthentication().getName())
        .orElse(null);
  }

  @GetMapping("/wiki")
  public String jsFetch() {
    return "jsFetch";
  }
}

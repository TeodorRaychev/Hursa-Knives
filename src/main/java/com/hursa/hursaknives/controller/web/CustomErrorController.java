package com.hursa.hursaknives.controller.web;

import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.service.UserService;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

  private final UserService userService;

  public CustomErrorController(UserService userService) {
    this.userService = userService;
  }

  @ModelAttribute("userProfile")
  public ProfileBindingModel getUserProfile() {
    return userService
        .getUserProfile(SecurityContextHolder.getContext().getAuthentication().getName())
        .orElse(null);
  }

  @RequestMapping("/error")
  public String handleError() {
    // do something like logging
    System.out.println("error controller");
    return "error";
  }
}

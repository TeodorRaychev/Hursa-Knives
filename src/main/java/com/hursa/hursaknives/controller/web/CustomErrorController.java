package com.hursa.hursaknives.controller.web;

import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
  public String handleError(HttpServletRequest request, Model model) {
    String statusCode = request.getAttribute("jakarta.servlet.error.status_code").toString();
    String errorMessage =
        switch (statusCode) {
          case "400" -> "Bad Request";
          case "401" -> "Unauthorized";
          case "403" -> "Access Denied";
          case "404" -> "Page Not Found";
          case "500" -> "Internal Server Error";
          default -> "Unexpected Error";
        };
    model.addAttribute("statusCode", statusCode);
    model.addAttribute("errorMessage", errorMessage);
    return "error";
  }
}

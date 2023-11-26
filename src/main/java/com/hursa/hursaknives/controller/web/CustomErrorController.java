package com.hursa.hursaknives.controller.web;

import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    String errorMessage = null;
    switch (statusCode) {
      case "400":
        model.addAttribute("statusCode", statusCode);
        errorMessage = "Bad Request";
        break;
      case "401":
        model.addAttribute("statusCode", statusCode);
        errorMessage = "Unauthorized";
        break;
      case "403":
        model.addAttribute("statusCode", statusCode);
        errorMessage = "Access Denied";
        break;
      case "404":
        model.addAttribute("statusCode", statusCode);
        errorMessage = "Page Not Found";
        break;
      case "500":
        model.addAttribute("statusCode", statusCode);
        errorMessage = "Internal Server Error";
        break;
      default:
        errorMessage = "Unexpected Error";
    }
    model.addAttribute("statusCode", statusCode);
    model.addAttribute("errorMessage", errorMessage);
    return "error";
  }
}

package com.hursa.hursaknives.controller.web;

import com.hursa.hursaknives.model.dto.RegistrationBindingModel;
import com.hursa.hursaknives.service.AuthService;
import com.hursa.hursaknives.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
  private final UserService userService;
  private final AuthService authService;

  public UserController(UserService userService, AuthService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/register")
  public String register() {
    return "register";
  }

  @PostMapping("/register")
  public String registerPost(
      RegistrationBindingModel registrationBindingModel, HttpSession session) {
    userService.registerUser(registrationBindingModel);

    authService.autoLogin(registrationBindingModel.email(), registrationBindingModel.password());
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.getName().equals("anonymousUser")) {
      SecurityContextHolder.getContext().setAuthentication(authentication);
      session.setAttribute(
          HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
          SecurityContextHolder.getContext());
    }
    return "redirect:/";
  }
}

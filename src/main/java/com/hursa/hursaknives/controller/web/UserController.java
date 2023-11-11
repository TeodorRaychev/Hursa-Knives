package com.hursa.hursaknives.controller.web;

import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.model.dto.RegistrationBindingModel;
import com.hursa.hursaknives.service.AuthService;
import com.hursa.hursaknives.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {
  private final UserService userService;
  private final AuthService authService;

  public UserController(UserService userService, AuthService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  @ModelAttribute("userProfile")
  public ProfileBindingModel getUserProfile() {
    return userService
        .getUserProfile(SecurityContextHolder.getContext().getAuthentication().getName())
        .orElse(null);
  }

  @ModelAttribute("successMessage")
  public String getSuccessMessage() {
    return null;
  }

  @ModelAttribute("errorMessage")
  public String getErrorMessage() {
    return null;
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/register")
  public String register(Model model) {
    if (!model.containsAttribute("registrationBindingModel")) {
      model.addAttribute(
          "registrationBindingModel", new RegistrationBindingModel(null, null, null, null, null));
    }
    return "register";
  }

  @PostMapping("/register")
  public String registerPost(
      @Valid RegistrationBindingModel registrationBindingModel,
      BindingResult bindingResult,
      RedirectAttributes rAtt,
      HttpSession session) {
    if (bindingResult.hasErrors()) {
      rAtt.addFlashAttribute("registrationBindingModel", registrationBindingModel);
      rAtt.addFlashAttribute(
          "org.springframework.validation.BindingResult.registrationBindingModel", bindingResult);
      rAtt.addFlashAttribute("errorMessage", "Unable to register");
      return "redirect:/users/register";
    }
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

  @GetMapping("/profile/{id}")
  public String profile(@PathVariable Long id) {
    return "profile";
  }

  @PostMapping("/profile/{id}")
  public String editProfile(
      @PathVariable Long id,
      @Valid ProfileBindingModel profileBindingModel,
      BindingResult bindingResult,
      RedirectAttributes rAtt) {
    if (bindingResult.hasErrors()) {
      rAtt.addFlashAttribute("userProfile", profileBindingModel);
      rAtt.addFlashAttribute(
          "org.springframework.validation.BindingResult.userProfile", bindingResult);
      rAtt.addFlashAttribute("errorMessage", "Unable to update profile");
      return "redirect:/users/profile/" + id;
    }
    ProfileBindingModel updatedProfileBindingModel =
        userService.updateUserProfile(profileBindingModel);
    rAtt.addFlashAttribute("userProfile", updatedProfileBindingModel);
    rAtt.addFlashAttribute("successMessage", "Profile updated successfully");
    return "redirect:/users/profile/" + id;
  }

  @PostMapping("/login-error")
  public String loginError(
      @ModelAttribute("email") String email,
      @ModelAttribute("rememberMe") String rememberMe,
      Model model) {
    model.addAttribute("email", email);
    if (rememberMe.isEmpty()) {
      rememberMe = null;
    }
    model.addAttribute("rememberMe", rememberMe);
    model.addAttribute("badCredentials", true);
    model.addAttribute("errorMessage", "Invalid email or password");
    return "login";
  }
}

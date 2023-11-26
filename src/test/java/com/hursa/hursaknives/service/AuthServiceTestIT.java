package com.hursa.hursaknives.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class AuthServiceTestIT {
  private final AuthService serviceToTest;
  private final Environment environment;

  @Autowired
  AuthServiceTestIT(AuthService serviceToTest, Environment environment) {
    this.serviceToTest = serviceToTest;
    this.environment = environment;
  }

  @Test
  void autoLogin() {
    String email = environment.getProperty("hursa.admin.email");
    String password = environment.getProperty("hursa.admin.password");
    serviceToTest.autoLogin(email, password);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication, "Authentication should not be null");
    assertEquals(
        email, authentication.getName(), "Authentication name should match provided email");
    assertEquals(
        password,
        authentication.getCredentials(),
        "Authentication credentials should match provided password");
  }
}

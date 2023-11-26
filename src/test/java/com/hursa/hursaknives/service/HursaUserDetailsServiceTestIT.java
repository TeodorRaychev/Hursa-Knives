package com.hursa.hursaknives.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class HursaUserDetailsServiceTestIT {
  private final HursaUserDetailsService serviceToTest;
  private final Environment environment;
  private final BCryptPasswordEncoder passwordEncoder;

  @Autowired
  HursaUserDetailsServiceTestIT(
      HursaUserDetailsService serviceToTest,
      Environment environment,
      BCryptPasswordEncoder passwordEncoder) {
    this.serviceToTest = serviceToTest;
    this.environment = environment;
    this.passwordEncoder = passwordEncoder;
  }

  @Test
  void loadUserByUsername() {
    String email = environment.getProperty("hursa.admin.email");
    assertDoesNotThrow(
        () -> serviceToTest.loadUserByUsername(email),
        "loadUserByUsername should not throw an exception");
    UserDetails userDetails = serviceToTest.loadUserByUsername(email);
    assertEquals(userDetails.getUsername(), email);
    assertTrue(
        passwordEncoder.matches(
            environment.getProperty("hursa.admin.password"), userDetails.getPassword()),
        "Should return correct password");
    assertTrue(
        userDetails.getAuthorities().stream()
            .anyMatch(role -> role.getAuthority().contains("ADMIN")),
        "Init admin should have ADMIN role");
    assertTrue(
        userDetails.getAuthorities().stream()
            .anyMatch(role -> role.getAuthority().contains("USER")),
        "Init admin should have USER role");
  }

  @Test
  void loadUserByUsernameShouldThrowWhenUserDoesNotExist() {
    String email = "test@test.com";
    UsernameNotFoundException usernameNotFoundException =
        assertThrows(
            UsernameNotFoundException.class,
            () -> serviceToTest.loadUserByUsername(email),
            "loadUserByUsername should throw an exception when user does not exist");
    assertEquals(
        "User " + email + " not found",
        usernameNotFoundException.getMessage(),
        "Incorrect exception message");
  }
}

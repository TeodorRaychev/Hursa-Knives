package com.hursa.hursaknives.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hursa.hursaknives.model.dto.RegistrationBindingModel;
import com.hursa.hursaknives.model.entity.UserEntity;
import com.hursa.hursaknives.model.enums.UserRoleEnum;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class UserServiceTestIT {

  private final UserService serviceToTest;
  private final BCryptPasswordEncoder passwordEncoder;

  @Autowired
  UserServiceTestIT(UserService serviceToTest, BCryptPasswordEncoder passwordEncoder) {
    this.serviceToTest = serviceToTest;
    this.passwordEncoder = passwordEncoder;
  }

  @Test
  void adminIsInitialized() {
    assertNull(serviceToTest.initAdmin("test", "test", "test@test.com", "test"));
  }

  @Test
  void registerUser() {
    String email = "test@test.com";
    String name = "test";
    String password = "test";
    RegistrationBindingModel registrationBindingModel =
        new RegistrationBindingModel(email, password, password, name, name, null);
    UserEntity userEntity = serviceToTest.registerUser(registrationBindingModel);
    assertEquals(email, userEntity.getEmail(), "Should return correct email");
    assertEquals(name, userEntity.getFirstName(), "Should return correct first name");
    assertEquals(name, userEntity.getLastName(), "Should return correct last name");
    assertTrue(
        userEntity.getRoles().stream().anyMatch(role -> role.equals(UserRoleEnum.USER)),
        "Should return correct roles");
    assertTrue(
        passwordEncoder.matches(password, userEntity.getPassword()),
        "Should return correct password");
  }

  @Test
  void deleteUser() {
    assertDoesNotThrow(
        () -> serviceToTest.deleteUser(1L), "deleteUser should not throw an exception");
    NoSuchElementException userShouldBeDeleted =
        assertThrows(
            NoSuchElementException.class,
            () -> serviceToTest.findById(1L),
            "User should be deleted");
    assertEquals("User not found", userShouldBeDeleted.getMessage(), "Incorrect exception message");
  }
}

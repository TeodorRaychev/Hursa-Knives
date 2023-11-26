package com.hursa.hursaknives.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.model.dto.RegistrationBindingModel;
import com.hursa.hursaknives.model.entity.UserEntity;
import com.hursa.hursaknives.model.enums.UserRoleEnum;
import com.hursa.hursaknives.repo.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.record.RecordModule;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
  private final ModelMapper modelMapper = new ModelMapper().registerModule(new RecordModule());
  private UserService serviceToTest;
  @Mock private UserRepository userRepository;

  @BeforeEach
  void setup() {
    serviceToTest = new UserService(userRepository, bCryptPasswordEncoder, modelMapper);
  }

  @Test
  void initAdmin() {
    String firstName = "test";
    String lastName = "test";
    String email = "test@test.com";
    String password = "test";
    String encoded = bCryptPasswordEncoder.encode(password);
    when(userRepository.count()).thenReturn(0L);
    when(userRepository.saveAndFlush(any(UserEntity.class)))
        .thenReturn(
            new UserEntity()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setPassword(encoded)
                .setRoles(Set.of(UserRoleEnum.ADMIN, UserRoleEnum.USER)));
    assertDoesNotThrow(
        () -> serviceToTest.initAdmin(firstName, lastName, email, password),
        "initAdmin should not throw an exception");
    UserEntity userEntity = serviceToTest.initAdmin(firstName, lastName, email, password);
    assertEquals(email, userEntity.getEmail(), "Should return correct email");
    assertEquals(firstName, userEntity.getFirstName(), "Should return correct first name");
    assertEquals(lastName, userEntity.getLastName(), "Should return correct last name");
    assertEquals(
        Set.of(UserRoleEnum.ADMIN, UserRoleEnum.USER),
        userEntity.getRoles(),
        "Should return correct roles");
    assertEquals(encoded, userEntity.getPassword(), "Should return correct password");
  }

  @Test
  void initAdminAlreadyExists() {
    when(userRepository.count()).thenReturn(1L);
    assertNull(
        serviceToTest.initAdmin("test", "test", "test@test.com", "test"), "Should return null");
  }

  @Test
  void registerUser() {
    String email = "test@test.com";
    String name = "test";
    String password = "password";
    RegistrationBindingModel registrationBindingModel =
        new RegistrationBindingModel(email, password, password, name, name, null);
    String encoded = bCryptPasswordEncoder.encode(password);
    when(userRepository.saveAndFlush(any(UserEntity.class)))
        .thenReturn(
            new UserEntity()
                .setEmail(email)
                .setPassword(encoded)
                .setFirstName(name)
                .setLastName(name)
                .setRoles(Set.of(UserRoleEnum.USER)));
    assertDoesNotThrow(
        () -> serviceToTest.registerUser(registrationBindingModel),
        "registerUser should not throw an exception");
    UserEntity userEntity = serviceToTest.registerUser(registrationBindingModel);
    assertEquals(email, userEntity.getEmail(), "Should return correct email");
    assertEquals(name, userEntity.getFirstName(), "Should return correct first name");
    assertEquals(name, userEntity.getLastName(), "Should return correct last name");
    assertEquals(Set.of(UserRoleEnum.USER), userEntity.getRoles(), "Should return correct roles");
    assertEquals(encoded, userEntity.getPassword(), "Should return correct password");
  }

  @Test
  void getUserProfile() {
    String existingEmail = "test@test.com";
    String nonExistingEmail = "notexist@test.com";
    when(userRepository.findByEmail(existingEmail))
        .thenReturn(
            Optional.of(
                new UserEntity()
                    .setEmail(existingEmail)
                    .setFirstName("test")
                    .setLastName("test")
                    .setRoles(Set.of(UserRoleEnum.ADMIN))));
    assertDoesNotThrow(
        () -> serviceToTest.getUserProfile(existingEmail),
        "getUserProfile should not throw an exception when user exists");
    assertFalse(
        serviceToTest.getUserProfile(nonExistingEmail).isPresent(),
        "Should be empty for non existing user");
    assertTrue(
        serviceToTest.getUserProfile(existingEmail).isPresent(),
        "Should not be empty for existing user");
  }

  @Test
  void updateUserProfileThrowsWhenUserWithIdDoesNotExist() {
    assertThrows(
        NoSuchElementException.class,
        () -> serviceToTest.updateUserProfile(new ProfileBindingModel().setId(1L)),
        "Should throw an exception");
  }

  @Test
  void updateUserProfileThrowsWhenPasswordsDoesNotMatch() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(new UserEntity().setPassword("test")));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            serviceToTest.updateUserProfile(
                new ProfileBindingModel()
                    .setId(1L)
                    .setPassword("test")
                    .setConfirmPassword("test1")),
        "Should throw an exception");
  }

  @Test
  void updateUserProfileThrowsWhenOldPasswordDoesNotMatch() {
    when(userRepository.findById(1L))
        .thenReturn(
            Optional.of(new UserEntity().setPassword(bCryptPasswordEncoder.encode("current"))));
    IllegalArgumentException exception =
        assertThrowsExactly(
            IllegalArgumentException.class,
            () ->
                serviceToTest.updateUserProfile(
                    new ProfileBindingModel()
                        .setId(1L)
                        .setOldPassword("notCurrent")
                        .setPassword("current")
                        .setConfirmPassword("current")),
            "Should throw an exception");
    assertEquals(
        "Incorrect current password", exception.getMessage(), "Incorrect exception message");
  }

  @Test
  void updateUserProfileThrowsWhenNewPasswordMatchesOld() {
    when(userRepository.findById(1L))
        .thenReturn(
            Optional.of(new UserEntity().setPassword(bCryptPasswordEncoder.encode("current"))));
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                serviceToTest.updateUserProfile(
                    new ProfileBindingModel()
                        .setId(1L)
                        .setOldPassword("current")
                        .setPassword("current")
                        .setConfirmPassword("current")),
            "Should throw an exception");
    assertEquals(
        "New password cannot be same as old password",
        exception.getMessage(),
        "Incorrect exception message");
  }

  @Test
  void updateUserProfileNewDetailsSameAsOld() {
    String name = "test";
    String email = "test@test.com";
    when(userRepository.findById(1L))
        .thenReturn(
            Optional.of(
                new UserEntity()
                    .setPassword(bCryptPasswordEncoder.encode("current"))
                    .setFirstName(name)
                    .setLastName(name)
                    .setEmail(email)));
    ProfileBindingModel profileBindingModel =
        new ProfileBindingModel()
            .setId(1L)
            .setOldPassword("current")
            .setFirstName(name)
            .setLastName(name)
            .setEmail(email);
    ProfileBindingModel updatedProfile = serviceToTest.updateUserProfile(profileBindingModel);
    assertNotEquals(profileBindingModel, updatedProfile, "Should not return same object");
    assertEquals(name, updatedProfile.getFirstName(), "Should not change first name");
    assertEquals(name, updatedProfile.getLastName(), "Should not change last name");
    assertEquals(email, updatedProfile.getEmail(), "Should not change email");
  }

  @Test
  void updateUserProfileNewDetailsDifferent() {
    String currentPass = "current";
    String newPass = "notCurrent";
    String name = "test";
    String email = "test@test.com";
    String firstName = "first";
    String lastName = "last";
    String newEmail = "new@test.com";
    when(userRepository.findById(1L))
        .thenReturn(
            Optional.of(
                new UserEntity()
                    .setPassword(bCryptPasswordEncoder.encode(currentPass))
                    .setFirstName(name)
                    .setLastName(name)
                    .setEmail(email)));
    ProfileBindingModel profileBindingModel =
        new ProfileBindingModel()
            .setId(1L)
            .setOldPassword(currentPass)
            .setPassword(newPass)
            .setConfirmPassword(newPass)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setEmail(newEmail);
    ProfileBindingModel updatedProfile = serviceToTest.updateUserProfile(profileBindingModel);
    assertNotEquals(profileBindingModel, updatedProfile, "Should not return same object");
    assertEquals(firstName, updatedProfile.getFirstName(), "Should change first name");
    assertEquals(lastName, updatedProfile.getLastName(), "Should change last name");
    assertEquals(newEmail, updatedProfile.getEmail(), "Should change email");
  }

  @Test
  void updateUserProfileRoles() {
    String name = "test";
    String email = "test@test.com";
    when(userRepository.findById(1L))
        .thenReturn(
            Optional.of(
                new UserEntity()
                    .setPassword(bCryptPasswordEncoder.encode("current"))
                    .setFirstName(name)
                    .setLastName(name)
                    .setEmail(email)
                    .setRoles(Set.of(UserRoleEnum.USER))));
    ProfileBindingModel profileBindingModel =
        new ProfileBindingModel()
            .setId(1L)
            .setOldPassword("current")
            .setFirstName(name)
            .setLastName(name)
            .setEmail(email)
            .setRoles(Set.of(UserRoleEnum.ADMIN, UserRoleEnum.USER));
    ProfileBindingModel updatedProfile = serviceToTest.updateUserProfile(profileBindingModel);
    assertNotEquals(profileBindingModel, updatedProfile, "Should not return same object");
    assertEquals(name, updatedProfile.getFirstName(), "Should not change first name");
    assertEquals(name, updatedProfile.getLastName(), "Should not change last name");
    assertEquals(email, updatedProfile.getEmail(), "Should not change email");
    assertEquals(
        Set.of(UserRoleEnum.ADMIN, UserRoleEnum.USER),
        updatedProfile.getRoles(),
        "New role should be added");
  }

  @Test
  void getAllUsers() {
    when(userRepository.findAll()).thenReturn(List.of(new UserEntity(), new UserEntity()));
    assertDoesNotThrow(
        () -> serviceToTest.getAllUsers(), "getAllUsers should not throw an exception");
    assertEquals(2, serviceToTest.getAllUsers().size(), "Should return 2 users");
  }

  @Test
  void getAllUsersEmpty() {
    assertDoesNotThrow(
        () -> serviceToTest.getAllUsers(), "getAllUsers should not throw an exception");
    assertEquals(0, serviceToTest.getAllUsers().size(), "Should return 0 users");
  }

  @Test
  void findByIdShouldThrowWhenUserDoesNotExist() {
    NoSuchElementException exception =
        assertThrows(
            NoSuchElementException.class,
            () -> serviceToTest.findById(1L),
            "Should throw an exception");
    assertEquals("User not found", exception.getMessage(), "Incorrect exception message");
  }

  @Test
  void findById() {
    String email = "test@test.com";
    String name = "test";
    when(userRepository.findById(1L))
        .thenReturn(
            Optional.of(
                new UserEntity()
                    .setEmail(email)
                    .setLastName(name)
                    .setFirstName(name)
                    .setRoles(Set.of(UserRoleEnum.ADMIN, UserRoleEnum.USER))));
    assertDoesNotThrow(() -> serviceToTest.findById(1L), "findById should not throw an exception");
    assertEquals(email, serviceToTest.findById(1L).getEmail(), "Should return correct email");
    assertEquals(
        name, serviceToTest.findById(1L).getFirstName(), "Should return correct first name");
    assertEquals(name, serviceToTest.findById(1L).getLastName(), "Should return correct last name");
    assertEquals(
        Set.of(UserRoleEnum.ADMIN, UserRoleEnum.USER),
        serviceToTest.findById(1L).getRoles(),
        "Should return correct roles");
  }
}

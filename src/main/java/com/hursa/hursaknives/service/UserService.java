package com.hursa.hursaknives.service;

import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.model.dto.RegistrationBindingModel;
import com.hursa.hursaknives.model.entity.UserEntity;
import com.hursa.hursaknives.model.enums.UserRoleEnum;
import com.hursa.hursaknives.repo.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;

  public UserService(
      UserRepository userRepository,
      BCryptPasswordEncoder bCryptPasswordEncoder,
      ModelMapper modelMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = bCryptPasswordEncoder;
    this.modelMapper = modelMapper;
  }

  public UserEntity initAdmin(String firstName, String lastName, String email, String password) {
    if (userRepository.count() == 0) {
      UserEntity userEntity =
          new UserEntity()
              .setFirstName(firstName)
              .setLastName(lastName)
              .setEmail(email)
              .setPassword(passwordEncoder.encode(password))
              .setRoles(Set.of(UserRoleEnum.ADMIN, UserRoleEnum.USER));
      return userRepository.saveAndFlush(userEntity);
    }
    return null;
  }

  public UserEntity registerUser(RegistrationBindingModel registrationBindingModel) {
    Assert.notNull(registrationBindingModel, "RegistrationBindingModel cannot be null");
    this.userRepository
        .findByEmail(registrationBindingModel.email())
        .ifPresent(
            user -> {
              throw new IllegalArgumentException(
                  "User with email " + user.getEmail() + " already exists");
            });
    assert registrationBindingModel.password() != null
        && !registrationBindingModel.password().isEmpty();
    UserEntity userEntity =
        modelMapper
            .map(registrationBindingModel, UserEntity.class)
            .setPassword(passwordEncoder.encode(registrationBindingModel.password()));
    if (userEntity.getRoles() == null || userEntity.getRoles().isEmpty()) {
      userEntity.setRoles(Set.of(UserRoleEnum.USER));
    }
    assert passwordEncoder.matches(registrationBindingModel.password(), userEntity.getPassword());
    assert userEntity.getPassword() != null;
    this.userRepository
        .findByEmail(registrationBindingModel.email())
        .ifPresent(
            user -> {
              throw new IllegalArgumentException(
                  "User with email " + user.getEmail() + " already exists");
            });
    return this.userRepository.saveAndFlush(userEntity);
  }

  public Optional<ProfileBindingModel> getUserProfile(String email) {
    Optional<UserEntity> userEntity = userRepository.findByEmail(email);
    return userEntity.map(user -> modelMapper.map(user, ProfileBindingModel.class));
  }

  public ProfileBindingModel updateUserProfile(ProfileBindingModel profileBindingModel) {
    Optional<UserEntity> optionalUserEntity = userRepository.findById(profileBindingModel.getId());
    if (optionalUserEntity.isEmpty()) {
      throw new NoSuchElementException(
          "User with id " + profileBindingModel.getId() + " not found");
    }
    if (profileBindingModel.getPassword() != null
        && profileBindingModel.getConfirmPassword() != null
        && !profileBindingModel.getPassword().equals(profileBindingModel.getConfirmPassword())) {
      throw new IllegalArgumentException("Passwords do not match");
    }
    UserEntity userEntity = optionalUserEntity.get();
    if (profileBindingModel.getOldPassword() != null
        && profileBindingModel.getPassword() != null
        && !profileBindingModel.getOldPassword().isEmpty()
        && !profileBindingModel.getPassword().isEmpty()) {
      if (!passwordEncoder.matches(
          profileBindingModel.getOldPassword(), userEntity.getPassword())) {
        throw new IllegalArgumentException("Incorrect current password");
      }
      if (passwordEncoder.matches(profileBindingModel.getPassword(), userEntity.getPassword())) {
        throw new IllegalArgumentException("New password cannot be same as old password");
      }
    }
    boolean isEdited = false;
    if (!profileBindingModel.getEmail().equals(userEntity.getEmail())
        || !profileBindingModel.getFirstName().equals(userEntity.getFirstName())
        || !profileBindingModel.getLastName().equals(userEntity.getLastName())) {
      isEdited = true;
      userEntity
          .setFirstName(profileBindingModel.getFirstName())
          .setLastName(profileBindingModel.getLastName())
          .setEmail(profileBindingModel.getEmail());
    }
    if (profileBindingModel.getPassword() != null
        && !profileBindingModel.getPassword().isEmpty()
        && !profileBindingModel.getPassword().equals(userEntity.getPassword())) {
      isEdited = true;
      userEntity.setPassword(passwordEncoder.encode(profileBindingModel.getPassword()));
    }
    if (profileBindingModel.getRoles() != null
        && !profileBindingModel.getRoles().isEmpty()
        && !profileBindingModel.getRoles().equals(userEntity.getRoles())) {
      isEdited = true;
      userEntity.setRoles(profileBindingModel.getRoles());
    }
    if (isEdited) {
      userRepository.saveAndFlush(userEntity);
    }
    return modelMapper.map(userEntity, ProfileBindingModel.class);
  }

  public List<UserEntity> getAllUsers() {
    return userRepository.findAll();
  }

  public ProfileBindingModel findById(Long id) {
    return modelMapper.map(
        userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User not found")),
        ProfileBindingModel.class);
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }
}

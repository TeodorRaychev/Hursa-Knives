package com.hursa.hursaknives.service;

import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.model.dto.RegistrationBindingModel;
import com.hursa.hursaknives.model.entity.UserEntity;
import com.hursa.hursaknives.model.enums.UserRoleEnum;
import com.hursa.hursaknives.repo.UserRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final Environment environment;
  private final BCryptPasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;

  public UserService(
      UserRepository userRepository,
      Environment environment,
      BCryptPasswordEncoder bCryptPasswordEncoder,
      ModelMapper modelMapper) {
    this.userRepository = userRepository;
    this.environment = environment;
    this.passwordEncoder = bCryptPasswordEncoder;
    this.modelMapper = modelMapper;
  }

  public void initAdmin() {
    if (userRepository.count() == 0) {

      UserEntity userEntity = new UserEntity();
      userEntity.setFirstName("Hursa");
      userEntity.setLastName("Admin");
      userEntity.setEmail(environment.getProperty("hursa.admin.email"));
      userEntity.setPassword(
          passwordEncoder.encode(environment.getProperty("hursa.admin.password")));
      userEntity.setRoles(Set.of(UserRoleEnum.ADMIN, UserRoleEnum.USER));
      userRepository.saveAndFlush(userEntity);
    }
  }

  public UserEntity registerUser(RegistrationBindingModel registrationBindingModel) {
    Assert.notNull(registrationBindingModel, "RegistrationBindingModel cannot be null");
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
    if (!profileBindingModel.getPassword().equals(profileBindingModel.getConfirmPassword())) {
      throw new IllegalArgumentException("Passwords do not match");
    }
    UserEntity userEntity = optionalUserEntity.get();
    if (!profileBindingModel.getOldPassword().isEmpty()
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
    if (!profileBindingModel.getPassword().isEmpty()
        && !profileBindingModel.getPassword().equals(userEntity.getPassword())) {
      isEdited = true;
      userEntity.setPassword(passwordEncoder.encode(profileBindingModel.getPassword()));
    }
    if (isEdited) {
      userRepository.saveAndFlush(userEntity);
    }
    return modelMapper.map(userEntity, ProfileBindingModel.class);
  }
}

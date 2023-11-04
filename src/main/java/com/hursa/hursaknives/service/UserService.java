package com.hursa.hursaknives.service;

import com.hursa.hursaknives.model.dto.RegistrationBindingModel;
import com.hursa.hursaknives.model.entity.UserEntity;
import com.hursa.hursaknives.model.enums.UserRoleEnum;
import com.hursa.hursaknives.repo.UserRepository;
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
}

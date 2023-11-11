package com.hursa.hursaknives.model.validation;

import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.model.entity.UserEntity;
import com.hursa.hursaknives.repo.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordFoundValidator
    implements ConstraintValidator<PasswordFound, ProfileBindingModel> {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private String field;
  private String message;

  public PasswordFoundValidator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void initialize(PasswordFound constraintAnnotation) {
    this.field = constraintAnnotation.field();
    this.message = constraintAnnotation.message();
  }

  @Override
  public boolean isValid(ProfileBindingModel value, ConstraintValidatorContext context) {
    if (value.getOldPassword().isEmpty()) {
      return true;
    }
    UserEntity userEntity =
        userRepository
            .findById(value.getId())
            .orElseThrow(
                () -> new IllegalArgumentException("User with id " + value.getId() + " not found"));
    boolean isValid = passwordEncoder.matches(value.getOldPassword(), userEntity.getPassword());
    if (!isValid) {
      context
          .buildConstraintViolationWithTemplate(message)
          .addPropertyNode(field)
          .addConstraintViolation()
          .disableDefaultConstraintViolation();
    }
    return isValid;
  }
}

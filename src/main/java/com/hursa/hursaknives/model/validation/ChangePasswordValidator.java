package com.hursa.hursaknives.model.validation;

import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.model.entity.UserEntity;
import com.hursa.hursaknives.repo.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ChangePasswordValidator
    implements ConstraintValidator<ChangePassword, ProfileBindingModel> {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private String field;
  private String message;

  public ChangePasswordValidator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void initialize(ChangePassword constraintAnnotation) {
    this.field = constraintAnnotation.field();
    this.message = constraintAnnotation.message();
  }

  @Override
  public boolean isValid(ProfileBindingModel value, ConstraintValidatorContext context) {
    UserEntity byId =
        userRepository
            .findById(value.getId())
            .orElseThrow(
                () -> new IllegalArgumentException("User with id " + value.getId() + " not found"));
    boolean isValid = value.getPassword().isEmpty() && value.getConfirmPassword().isEmpty();
    if (passwordEncoder.matches(value.getOldPassword(), byId.getPassword())
        && !passwordEncoder.matches(value.getPassword(), byId.getPassword())
        && value.getPassword().equals(value.getConfirmPassword())) {
      isValid = true;
    }
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

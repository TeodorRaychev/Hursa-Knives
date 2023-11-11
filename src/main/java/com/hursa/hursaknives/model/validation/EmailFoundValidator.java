package com.hursa.hursaknives.model.validation;

import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.model.entity.UserEntity;
import com.hursa.hursaknives.repo.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailFoundValidator implements ConstraintValidator<EmailFound, ProfileBindingModel> {
  private final UserRepository userRepository;
  private String field;
  private String message;

  public EmailFoundValidator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void initialize(EmailFound constraintAnnotation) {
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
    boolean isValid;
    if (byId.getEmail().equals(value.getEmail())) {
      isValid = true;
    } else {
      isValid = userRepository.findByEmail(value.getEmail()).isEmpty();
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

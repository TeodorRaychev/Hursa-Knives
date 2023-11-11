package com.hursa.hursaknives.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

  private String field;
  private String fieldMatch;
  private String message;

  @Override
  public void initialize(FieldMatch constraintAnnotation) {
    this.field = constraintAnnotation.field();
    this.fieldMatch = constraintAnnotation.fieldMatch();
    this.message = constraintAnnotation.message();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);

    Object firstProperty = beanWrapper.getPropertyValue(this.field);
    Object secondProperty = beanWrapper.getPropertyValue(this.fieldMatch);

    boolean isValid = Objects.equals(firstProperty, secondProperty);

    if (!isValid) {
      context
          .buildConstraintViolationWithTemplate(message)
          .addPropertyNode(fieldMatch)
          .addConstraintViolation()
          .disableDefaultConstraintViolation();
    }

    return isValid;
  }
}

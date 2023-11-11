package com.hursa.hursaknives.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = PasswordFoundValidator.class)
public @interface PasswordFound {
  String field();

  String message() default "Current password does not match!";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

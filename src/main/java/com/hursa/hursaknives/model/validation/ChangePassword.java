package com.hursa.hursaknives.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = ChangePasswordValidator.class)
public @interface ChangePassword {
  String field() default "password";

  String message() default
      "To keep old password leave the fields blank. New password cannot be same as old password.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

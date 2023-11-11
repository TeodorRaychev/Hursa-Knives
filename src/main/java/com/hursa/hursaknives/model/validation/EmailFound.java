package com.hursa.hursaknives.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = EmailFoundValidator.class)
public @interface EmailFound {
  String field() default "email";

  String message() default "Email should match current user email or be unique!";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

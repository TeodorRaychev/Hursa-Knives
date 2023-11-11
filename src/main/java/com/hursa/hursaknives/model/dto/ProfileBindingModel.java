package com.hursa.hursaknives.model.dto;

import com.hursa.hursaknives.model.validation.ChangePassword;
import com.hursa.hursaknives.model.validation.EmailFound;
import com.hursa.hursaknives.model.validation.FieldMatch;
import com.hursa.hursaknives.model.validation.PasswordFound;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@FieldMatch(field = "password", fieldMatch = "confirmPassword", message = "Passwords must match")
@ChangePassword
@EmailFound
@PasswordFound(field = "oldPassword")
public class ProfileBindingModel {
  Long id;

  @Size(min = 2, message = "First name must be at least 2 characters")
  String firstName;

  @Size(min = 2, message = "Last name must be at least 2 characters")
  String lastName;

  @NotEmpty(message = "Email cannot be empty!")
  @Email
  String email;

  @NotNull String oldPassword;
  @NotNull String password;
  @NotNull String confirmPassword;
}

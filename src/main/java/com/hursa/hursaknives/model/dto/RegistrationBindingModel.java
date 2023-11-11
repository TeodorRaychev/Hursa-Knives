package com.hursa.hursaknives.model.dto;

import com.hursa.hursaknives.model.validation.FieldMatch;
import com.hursa.hursaknives.model.validation.UniqueUserEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@FieldMatch(field = "password", fieldMatch = "confirmPassword", message = "Passwords must match")
public record RegistrationBindingModel(
    @NotEmpty(message = "Email cannot be empty")
        @Email(message = "Invalid email")
        @UniqueUserEmail(message = "Email already in use")
        String email,
    @Size(min = 5, message = "Password must be at least 5 characters") String password,
    @Size(min = 5, message = "Confirm password must be at least 5 characters")
        String confirmPassword,
    @Size(min = 2, message = "First name must be at least 2 characters") String firstName,
    @Size(min = 2, message = "Last name must be at least 2 characters") String lastName) {}

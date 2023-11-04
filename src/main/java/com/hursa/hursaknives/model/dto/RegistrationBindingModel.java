package com.hursa.hursaknives.model.dto;

import com.hursa.hursaknives.model.enums.UserRoleEnum;
import java.util.Set;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public record RegistrationBindingModel(
    String email,
    String password,
    String confirmPassword,
    String firstName,
    String lastName,
    Set<UserRoleEnum> roles) {}

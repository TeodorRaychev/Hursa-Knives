package com.hursa.hursaknives.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ContactDTO {
  private Long id;

  @Size(min = 2, message = "First name must be at least 2 characters long")
  private String firstName;

  @Size(min = 2, message = "Last name must be at least 2 characters long")
  private String lastName;

  @Email
  @NotEmpty(message = "Email cannot be empty!")
  private String email;

  private String phone;
  private String additionalInfo;
  private String imageUrl;
}

package com.hursa.hursaknives.model.dto;

import com.hursa.hursaknives.model.enums.UserRoleEnum;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class UserDTO {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private Set<UserRoleEnum> roles;
}

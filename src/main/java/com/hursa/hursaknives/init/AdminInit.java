package com.hursa.hursaknives.init;

import com.hursa.hursaknives.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AdminInit implements CommandLineRunner {

  private final UserService userService;
  private final Environment environment;

  public AdminInit(UserService userService, Environment environment) {
    this.userService = userService;
    this.environment = environment;
  }

  @Override
  public void run(String... args) throws Exception {
    String email = environment.getProperty("hursa.admin.email");
    assert email != null;
    assert !email.isEmpty();
    String password = environment.getProperty("hursa.admin.password");
    assert password != null;
    assert !password.isEmpty();
    userService.initAdmin("Hursa", "Admin", email, password);
  }
}

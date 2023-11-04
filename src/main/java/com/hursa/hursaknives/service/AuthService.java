package com.hursa.hursaknives.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;

  @Autowired
  public AuthService(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  public void autoLogin(String username, String password) {
    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(username, password);
    Authentication authentication = authenticationManager.authenticate(token);
    // Set the authentication object in SecurityContext
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}

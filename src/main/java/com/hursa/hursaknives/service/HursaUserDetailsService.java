package com.hursa.hursaknives.service;

import com.hursa.hursaknives.model.entity.UserEntity;
import com.hursa.hursaknives.repo.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class HursaUserDetailsService implements UserDetailsService {

  public final UserRepository userRepository;

  public HursaUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository
        .findByEmail(email)
        .map(this::map)
        .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
  }

  private UserDetails map(UserEntity userEntity) {
    return User.withUsername(userEntity.getEmail())
        .password(userEntity.getPassword())
        .authorities(
            userEntity.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getRole().name()))
                .toList())
        .build();
  }
}

package com.hursa.hursaknives.config;

import com.hursa.hursaknives.model.enums.UserRoleEnum;
import com.hursa.hursaknives.repo.UserRepository;
import com.hursa.hursaknives.service.HursaUserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  private final UserRepository userRepository;
  private final Environment environment;

  public SecurityConfig(UserRepository userRepository, Environment environment) {
    this.userRepository = userRepository;
    this.environment = environment;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .authorizeHttpRequests(
            authorizeRequests ->
                authorizeRequests
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                    .permitAll()
                    .requestMatchers("/")
                    .permitAll()
                    .requestMatchers("/users/login", "/users/login-error")
                    .anonymous()
                    .requestMatchers("/users/profile/**")
                    .authenticated()
                    .requestMatchers("/users/register")
                    .anonymous()
                    .requestMatchers("/users/register")
                    .authenticated()
                    .requestMatchers("/error")
                    .permitAll()
                    .requestMatchers("/products")
                    .permitAll()
                    .requestMatchers("/wiki")
                    .permitAll()
                    .requestMatchers("/contacts")
                    .permitAll()
                    .requestMatchers("/admin", "*/admin/**")
                    .hasRole(UserRoleEnum.ADMIN.name())
                    .anyRequest()
                    .authenticated())
        .formLogin(
            formLogin ->
                formLogin
                    .loginPage("/users/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/")
                    .failureForwardUrl("/users/login-error"))
        .logout(
            logout ->
                logout.logoutUrl("/users/logout").logoutSuccessUrl("/").invalidateHttpSession(true))
        .sessionManagement(
            sessionManagement ->
                sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .sessionFixation()
                    .migrateSession()
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
                    .expiredUrl("/users/login?expired"))
        .rememberMe(
            rememberMe ->
                rememberMe
                    .key(environment.getProperty("hursa.rememberme.key"))
                    .rememberMeParameter("rememberMe")
                    .tokenValiditySeconds(60 * 60 * 24 * 30)
                    .rememberMeCookieName("rememberMe"))
        .build();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService(UserRepository userRepository) {
    return new HursaUserDetailsService(userRepository);
  }

  @Bean
  public AuthenticationManager authenticationManager() throws Throwable {

    return authenticationManagerBuilder().build();
  }

  private AuthenticationManagerBuilder authenticationManagerBuilder() throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        new AuthenticationManagerBuilder(
            new ObjectPostProcessor<>() {
              @Override
              public <O> O postProcess(O object) {
                return object;
              }
            });
    authenticationManagerBuilder.eraseCredentials(false);
    authenticationManagerBuilder
        .userDetailsService(userDetailsService(userRepository))
        .passwordEncoder(passwordEncoder());
    return authenticationManagerBuilder;
  }
}

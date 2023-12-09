package com.hursa.hursaknives.config;

import com.hursa.hursaknives.interceptor.CustomLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  private final CustomLoggingInterceptor customLoggingInterceptor;

  public WebMvcConfig(CustomLoggingInterceptor customLoggingInterceptor) {
    this.customLoggingInterceptor = customLoggingInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(customLoggingInterceptor);
  }
}

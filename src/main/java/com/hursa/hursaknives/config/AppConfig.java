package com.hursa.hursaknives.config;

import com.hursa.hursaknives.interceptor.CustomLoggingInterceptor;
import org.modelmapper.ModelMapper;
import org.modelmapper.record.RecordModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper().registerModule(new RecordModule());
  }

  @Bean
  public CustomLoggingInterceptor registrationInterceptor() {
    return new CustomLoggingInterceptor();
  }
}

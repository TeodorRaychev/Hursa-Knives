package com.hursa.hursaknives.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
  @Value("${cloudinary.cloudName}")
  private String CLOUD_NAME;

  @Value("${cloudinary.apiKey}")
  private String API_KEY;

  @Value("${cloudinary.apiSecret}")
  private String API_SECRET;

  @Bean
  public Cloudinary cloudinary() {
    return new Cloudinary(
        ObjectUtils.asMap("cloud_name", CLOUD_NAME, "api_key", API_KEY, "api_secret", API_SECRET));
  }
}

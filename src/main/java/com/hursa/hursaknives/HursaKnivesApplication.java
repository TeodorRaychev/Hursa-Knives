package com.hursa.hursaknives;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HursaKnivesApplication {

  public static void main(String[] args) {
    SpringApplication.run(HursaKnivesApplication.class, args);
  }
}

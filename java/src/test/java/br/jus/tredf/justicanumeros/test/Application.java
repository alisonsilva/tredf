package br.jus.tredf.justicanumeros.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages="br.jus.tredf.justicanumeros")
@EnableAutoConfiguration
@EnableWebMvc
public class Application { 

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}

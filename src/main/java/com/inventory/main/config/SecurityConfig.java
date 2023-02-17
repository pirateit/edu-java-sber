package com.inventory.main.config;

import com.inventory.main.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
      .authorizeRequests()
      .antMatchers("/", "/assets/**").permitAll()
      .antMatchers("/settings/**", "/items/create/**").hasAnyRole(User.Role.OWNER.name(), User.Role.ADMIN.name())
      .antMatchers("/dashboard/**", "/items/**", "/movements/**", "/profile/**", "/**").authenticated()
      .and()
      .formLogin()
      .loginPage("/")
      .loginProcessingUrl("/login")
      .defaultSuccessUrl("/items")
      .usernameParameter("login")
      .and()
      .csrf()
      .disable()
      .logout()
      .logoutSuccessUrl("/")
      .and()
      .build();
  }

}

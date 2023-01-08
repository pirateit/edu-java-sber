package com.inventory.main.config;

import com.inventory.main.user.User;
import com.inventory.main.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

//    @Bean
//    public UserDetailsService userDetailsService(UserRepository userRepo) {
//        return email -> {
//            User user = userRepo.findByEmail(email);
//            if (user != null) {
//                return user;
//            }
//
//            throw new UsernameNotFoundException("User not found");
//        };
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .antMatchers("/settings/**").hasAnyRole(User.Role.OWNER.name(), User.Role.ADMIN.name())
                .antMatchers("/dashboard/**", "/items/**", "/movements/**").authenticated()
                .antMatchers("/", "/**").permitAll()
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

package com.kmarinos.externalsqltablemonitoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
public class ExampleSecurityConfig {
@Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
  http.csrf().disable()
      .authorizeHttpRequests(c->c.requestMatchers("/actuator","/actuator/**").permitAll())
      .authorizeHttpRequests(c->c.requestMatchers("/error").permitAll())
      .authorizeHttpRequests(c->c.requestMatchers("/api/*/resource").hasAnyAuthority("READ_RESOURCE","ROLE_ADMIN"))
      .authorizeHttpRequests().requestMatchers("/api/**").hasAnyRole("USER","ADMIN").and()
      .authorizeHttpRequests().requestMatchers("/**").hasAnyRole("ADMIN").and()
      .authorizeHttpRequests().anyRequest().authenticated().and()
      .httpBasic(Customizer.withDefaults());
  return http.build();
}
@Bean
  public UserDetailsService userDetailsService(){
  UserDetails user = User.builder()
      .username("user")
      .password("pass")
      .passwordEncoder(passwordEncoder()::encode)
      .roles("USER")
      .build();
  UserDetails userResources = User.builder()
      .username("user-resources")
      .password("pass")
      .passwordEncoder(passwordEncoder()::encode)
      .roles("USER")
      .authorities("READ_RESOURCE")
      .build();
  UserDetails admin = User.builder()
      .username("admin")
      .password("pass")
      .passwordEncoder(passwordEncoder()::encode)
      .roles("ADMIN")
      .build();
  return new InMemoryUserDetailsManager(user,userResources,admin);
}
@Bean
  public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();
}
}

package com.fitnessApplication.config;

import com.fitnessApplication.security.JwtRequestFilter;
import com.fitnessApplication.security.jwtAuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

     @Autowired
     private JwtRequestFilter JwtRequestFilter;
     @Autowired
     private jwtAuthEntryPoint jwtAuthEntryPoint;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http
                 .sessionManagement(session->session
                         .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                 .authorizeHttpRequests(auth->auth.requestMatchers(
                         "/api/auth/register",
                            "/api/auth/login",
                              "/api/auth/refresh"

                         ).permitAll()
                         .anyRequest().authenticated()
                 )
        .exceptionHandling(ex->ex.authenticationEntryPoint(jwtAuthEntryPoint));
        http.addFilterBefore(JwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.csrf(csrf->csrf.disable());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}


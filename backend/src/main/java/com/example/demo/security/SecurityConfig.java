package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
        		.cors(cors -> {})
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                		.requestMatchers("/images/**").permitAll()
                        .requestMatchers(
                                "/api/customer/register",
                                "/api/customer/login",
                                "/api/car-types/{hubId}",
                                "/api/hubs",
                                "/api/hubs/{hubId}",
                                "/api/addons",
                                "/api/addons/{hubId}",
                                "/api/airports",
                                "/api/airports/city/{cityId}",
                                "/api/airports/search",
                                "/api/states",
                                "/api/cities",
                                "/api/cities/state/{stateId}",
                                "/api/cars",
                                "/api/cars/{carId}",
                                "/health")
                        .permitAll()

                        .anyRequest()
                        .authenticated())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider)

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
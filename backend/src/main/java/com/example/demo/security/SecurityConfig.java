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

                	    // Public APIs
                	    .requestMatchers(
                	        "/api/customer/register",
                	        "/api/customer/login",
                	        "/api/staff/register",
                	        "/api/staff/login",
                	        "/health"
                	    ).permitAll()

                	    // Customer APIs
                	    .requestMatchers("/api/customer/**")
                	    .hasRole("CUSTOMER")

                	    .requestMatchers("/api/bookings/**")
                	    .hasRole("CUSTOMER")

                	    // Staff APIs
                	    .requestMatchers("/api/staff/bookings/**")
                	    .hasRole("STAFF")

                	    .requestMatchers("/api/staff/**")
                	    .hasRole("STAFF")

                	    // Everything else
                	    .anyRequest()
                	    .authenticated()
                	)

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
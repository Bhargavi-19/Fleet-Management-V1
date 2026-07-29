package com.example.demo.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import org.springframework.http.HttpMethod;
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

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    /**
     * Only present when Google credentials are configured.
     *
     * ObjectProvider lets us ask "is this bean there?" without the injection
     * failing when it is not, so the application starts fine on a machine that
     * has no OAuth2 client id and secret. Google sign-in is simply switched off.
     */
    @Autowired
    private ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
        		.cors(cors -> {})
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                	    // CORS pre-flight must never be blocked
                	    .requestMatchers(HttpMethod.OPTIONS, "/**")
                	    .permitAll()

                	    // Public APIs - authentication
                	    .requestMatchers(
                	        "/api/customer/register",
                	        "/api/customer/login",
                	        "/api/staff/register",
                	        "/api/staff/login",
                	        "/health"
                	    ).permitAll()

                	    // Google Sign-In (SSO).
                	    // /oauth2/authorization/google starts the flow and
                	    // /login/oauth2/code/google is where Google comes back to.
                	    .requestMatchers(
                	        "/oauth2/**",
                	        "/login/oauth2/**"
                	    ).permitAll()

                	    // Public APIs - guest booking
                	    // A visitor can book without creating an account.
                	    .requestMatchers(HttpMethod.POST, "/api/bookings/guest")
                	    .permitAll()

                	    // Public APIs - reference / master data
                	    // These feed the landing page and the booking funnel,
                	    // which both run before the user logs in.
                	    .requestMatchers(
                	        HttpMethod.GET,
                	        "/api/states/**",
                	        "/api/cities/**",
                	        "/api/hubs/**",
                	        "/api/airports/**",
                	        "/api/car-types/**",
                	        "/api/cars/**",
                	        "/api/addons/**"
                	    ).permitAll()

                	    // Staff APIs
                	    // NOTE: declared before /api/customer/** and /api/bookings/**
                	    // only for readability - order within the same prefix matters.
                	    .requestMatchers("/api/staff/bookings/**")
                	    .hasRole("STAFF")

                	    .requestMatchers("/api/staff/handover/**")
                	    .hasRole("STAFF")

                	    .requestMatchers("/api/staff/**")
                	    .hasRole("STAFF")

                	    // Customer APIs
                	    .requestMatchers("/api/customer/**")
                	    .hasRole("CUSTOMER")

                	    .requestMatchers("/api/bookings/**")
                	    .hasRole("CUSTOMER")

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

        // Google Sign-In, only when credentials are configured.
        //
        // On success OAuth2SuccessHandler issues our own JWT and redirects
        // back to the frontend, so the rest of the app keeps using exactly
        // the same token as a normal password login.
        if (clientRegistrationRepository.getIfAvailable() != null) {

            http.oauth2Login(oauth -> oauth
                    .successHandler(oAuth2SuccessHandler));
        }

        return http.build();
    }
}
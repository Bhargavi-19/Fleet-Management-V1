package com.example.demo.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.entity.base.Customer;
import com.example.demo.entity.base.Staff;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.StaffRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Runs after Google has authenticated the user.
 *
 * The rest of the application is stateless and JWT based, so this handler
 * turns the Google identity into one of our own JWTs and hands it back to the
 * frontend. From that point on a Google user is indistinguishable from a user
 * who signed in with a password.
 *
 * FLOW
 *   1. Frontend sends the user to  GET /oauth2/authorization/google
 *   2. Google authenticates them and calls back to
 *      /login/oauth2/code/google
 *   3. Spring Security completes the exchange and calls this handler
 *   4. We look the e-mail up:
 *        - existing staff    -> issue a STAFF token
 *        - existing customer -> issue a CUSTOMER token
 *        - nobody yet        -> create a customer record, then issue a token
 *   5. Redirect to  {frontend}/oauth-success?token=...&role=...
 *   6. The frontend stores the token and carries on as normal
 */
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log =
            LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    /** Where to send the browser afterwards. Configurable per environment. */
    @Value("${app.oauth2.redirect-uri:http://localhost:5173/oauth-success}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User googleUser = (OAuth2User) authentication.getPrincipal();

        String email = googleUser.getAttribute("email");

        if (email == null || email.isBlank()) {
            log.warn("Google sign-in returned no email address");
            redirectWithError(response, "Google did not share an email address.");
            return;
        }

        // ---------------------------------------------------------
        // 1. Staff sign in with Google too - check them first, the
        //    same order CustomUserDetailsService uses.
        // ---------------------------------------------------------
        Staff staff = staffRepository.findByEmail(email).orElse(null);

        if (staff != null) {
            log.info("Google sign-in matched staff account {}", email);
            redirectWithToken(response, jwtService.generateToken(email), "STAFF");
            return;
        }

        // ---------------------------------------------------------
        // 2. Existing customer
        // ---------------------------------------------------------
        Customer customer = customerRepository.findByEmail(email).orElse(null);

        // ---------------------------------------------------------
        // 3. First time here - create the customer record.
        //
        //    Without this, a brand new Google user would just be told
        //    "not registered", which defeats the point of single sign-on.
        //    The profile is deliberately minimal; the customer completes it
        //    on the My Information page or during their first booking.
        // ---------------------------------------------------------
        if (customer == null) {
            customer = createCustomerFromGoogle(googleUser, email);
            log.info("Created a new customer from Google sign-in: {}", email);
        }

        redirectWithToken(response, jwtService.generateToken(email), "CUSTOMER");
    }

    /** Builds a minimal customer from the Google profile. */
    private Customer createCustomerFromGoogle(OAuth2User googleUser, String email) {

        Customer customer = new Customer();

        customer.setCustomerId(UUID.randomUUID().toString());
        customer.setEmail(email);

        // Google gives "given_name" / "family_name"; fall back to "name".
        String firstName = googleUser.getAttribute("given_name");
        String lastName = googleUser.getAttribute("family_name");

        if (firstName == null || firstName.isBlank()) {
            String fullName = googleUser.getAttribute("name");
            if (fullName != null && !fullName.isBlank()) {
                String[] parts = fullName.trim().split("\\s+", 2);
                firstName = parts[0];
                if (parts.length > 1 && (lastName == null || lastName.isBlank())) {
                    lastName = parts[1];
                }
            } else {
                firstName = email.split("@")[0];
            }
        }

        customer.setFirstName(firstName);
        customer.setLastName(lastName);

        // phone is NOT NULL + UNIQUE in the schema, and Google never gives us
        // one. A unique placeholder keeps the insert legal; the customer
        // replaces it on the My Information page.
        customer.setPhone(placeholderPhone());

        // No password: this account can only ever sign in through Google.
        customer.setPasswordHash(null);
        customer.setIsActive(true);

        return customerRepository.save(customer);
    }

    /**
     * A unique, obviously-fake phone number.
     * Starts with 0 so it can never collide with a real Indian mobile, which
     * our validation requires to start 6-9.
     */
    private String placeholderPhone() {
        String candidate;
        do {
            candidate = "0" + String.valueOf(System.nanoTime()).substring(0, 9);
        } while (customerRepository.findByPhone(candidate).isPresent());
        return candidate;
    }

    private void redirectWithToken(
            HttpServletResponse response,
            String token,
            String role) throws IOException {

        response.sendRedirect(redirectUri
                + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                + "&role=" + role);
    }

    private void redirectWithError(
            HttpServletResponse response,
            String message) throws IOException {

        response.sendRedirect(redirectUri
                + "?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }
}

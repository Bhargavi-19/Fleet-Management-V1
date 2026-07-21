package com.example.demo.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.UpdateCustomerRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.ProfileResponse;
import com.example.demo.entity.base.Customer;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public ApiResponse<String> register(RegisterRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse<>(false, "Email already exists", null);
        }

        if (customerRepository.existsByPhone(request.getPhone())) {
            return new ApiResponse<>(false, "Phone already exists", null);
        }

        Customer customer = new Customer();

        customer.setCustomerId(UUID.randomUUID().toString());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        // Encrypt password before saving
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        customer.setIsActive(true);

        customerRepository.save(customer);

        return new ApiResponse<>(true, "Customer registered successfully", null);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.getEmail(),
                        request.getPassword()

                ));

        Customer customer = customerRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        String token =
                jwtService.generateToken(customer.getEmail());

        return new LoginResponse(

                token,

                "Bearer",

                customer.getCustomerId(),

                customer.getEmail()

        );
    }

    @Override
    public ProfileResponse getProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Customer customer = customerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        ProfileResponse response = new ProfileResponse();

        response.setCustomerId(customer.getCustomerId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        response.setDrivingLicenseNo(customer.getDrivingLicenseNo());
        response.setPassportNo(customer.getPassportNo());

        return response;
    }
    
    @Override
    public ApiResponse<ProfileResponse> updateProfile(UpdateCustomerRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Customer customer = customerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhone(request.getPhone());
        customer.setDrivingLicenseNo(request.getDrivingLicenseNo());
        customer.setPassportNo(request.getPassportNo());

        customerRepository.save(customer);

        ProfileResponse response = new ProfileResponse();

        response.setCustomerId(customer.getCustomerId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail()); // Read-only
        response.setPhone(customer.getPhone());
        response.setDrivingLicenseNo(customer.getDrivingLicenseNo());
        response.setPassportNo(customer.getPassportNo());

        return new ApiResponse<>(
                true,
                "Profile updated successfully",
                response
        );
    }

    @Override
    public ApiResponse<String> changePassword(ChangePasswordRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Customer customer = customerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        // Verify old password
        if (!passwordEncoder.matches(
                request.getOldPassword(),
                customer.getPasswordHash())) {

            throw new RuntimeException("Old password is incorrect");
        }

        // Verify new password & confirm password
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new RuntimeException(
                    "New password and Confirm password do not match");
        }

        // Prevent using the same password again
        if (passwordEncoder.matches(
                request.getNewPassword(),
                customer.getPasswordHash())) {

            throw new RuntimeException(
                    "New password cannot be the same as the old password");
        }

        // Save new password
        customer.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword()));

        customerRepository.save(customer);

        return new ApiResponse<>(
                true,
                "Password changed successfully",
                null);
    }
    
}

package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
=======
import com.example.demo.exception.error.BusinessException;
import com.example.demo.exception.error.ResourceNotFoundException;
>>>>>>> Developer
import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.UpdateCustomerRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.ProfileResponse;
<<<<<<< HEAD
import com.example.demo.entity.base.Customer;
import com.example.demo.repository.CustomerRepository;
=======
import com.example.demo.entity.base.City;
import com.example.demo.entity.base.Customer;
import com.example.demo.entity.base.State;
import com.example.demo.exception.error.UnauthorizedActionException;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.StateRepository;
>>>>>>> Developer
import com.example.demo.response.ApiResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.CustomerService;

import jakarta.transaction.Transactional;

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

<<<<<<< HEAD
    @Override
    public ApiResponse<String> register(RegisterRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse<>(false, "Email already exists", null);
        }

        if (customerRepository.existsByPhone(request.getPhone())) {
            return new ApiResponse<>(false, "Phone already exists", null);
=======
    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private StateRepository stateRepository;

    @Override
    public ApiResponse<String> register(RegisterRequest request) {

        // Throwing here (instead of returning success=false with HTTP 200)
        // means the frontend gets a real 400 and shows the error.
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("This email is already registered");
        }

        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("This phone number is already registered");
>>>>>>> Developer
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
<<<<<<< HEAD
    public LoginResponse login(LoginRequest request) {
=======
    public ApiResponse<LoginResponse> login(LoginRequest request) {
>>>>>>> Developer

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.getEmail(),
                        request.getPassword()

                ));

        Customer customer = customerRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
<<<<<<< HEAD
                        new RuntimeException("Customer not found"));
=======
                        new ResourceNotFoundException("Customer not found"));
>>>>>>> Developer

        String token =
                jwtService.generateToken(customer.getEmail());

<<<<<<< HEAD
        return new LoginResponse(
=======
        LoginResponse response = new LoginResponse(
>>>>>>> Developer

                token,

                "Bearer",

                customer.getCustomerId(),

                customer.getEmail()

        );
<<<<<<< HEAD
    }

    @Override
    public ProfileResponse getProfile() {
=======

        return new ApiResponse<>(true, "Login successful", response);
    }

    /** Loads the customer behind the current JWT. */
    private Customer getLoggedInCustomer() {
>>>>>>> Developer

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

<<<<<<< HEAD
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
=======
        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new UnauthorizedActionException(
                    "Customer is not authenticated");
        }

        return customerRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found"));
    }

    @Override
    @Transactional
    public ApiResponse<ProfileResponse> getProfile() {

        ProfileResponse response =
                ProfileResponse.fromEntity(getLoggedInCustomer());

        return new ApiResponse<>(
                true,
                "Profile fetched successfully",
                response);
    }

    @Override
    @Transactional
    public ApiResponse<ProfileResponse> updateProfile(UpdateCustomerRequest request) {

        Customer customer = getLoggedInCustomer();

        // Only overwrite the fields the caller actually sent, so a screen
        // that edits one section does not wipe out the rest.
        if (request.getFirstName() != null) {
            customer.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            customer.setLastName(request.getLastName());
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()) {

            boolean phoneTaken =
                    customerRepository
                            .findByPhone(request.getPhone())
                            .filter(other -> !other.getCustomerId()
                                    .equals(customer.getCustomerId()))
                            .isPresent();

            if (phoneTaken) {
                throw new BusinessException(
                        "This phone number is already in use");
            }

            customer.setPhone(request.getPhone());
        }

        if (request.getDateOfBirth() != null) {
            customer.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getGender() != null) {
            customer.setGender(request.getGender());
        }

        if (request.getNationality() != null) {
            customer.setNationality(request.getNationality());
        }

        if (request.getDrivingLicenseNo() != null) {
            customer.setDrivingLicenseNo(request.getDrivingLicenseNo());
        }

        if (request.getPassportNo() != null) {
            customer.setPassportNo(request.getPassportNo());
        }

        if (request.getAddressLine1() != null) {
            customer.setAddressLine1(request.getAddressLine1());
        }

        if (request.getAddressLine2() != null) {
            customer.setAddressLine2(request.getAddressLine2());
        }

        if (request.getPincode() != null) {
            customer.setPincode(request.getPincode());
        }

        if (request.getDocumentType() != null) {
            customer.setDocumentType(request.getDocumentType());
        }

        if (request.getStateId() != null) {

            State state = stateRepository
                    .findById(request.getStateId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("State not found"));

            customer.setState(state);
        }

        if (request.getCityId() != null) {

            City city = cityRepository
                    .findById(request.getCityId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("City not found"));

            customer.setCity(city);
        }

        customerRepository.save(customer);

        return new ApiResponse<>(
                true,
                "Profile updated successfully",
                ProfileResponse.fromEntity(customer));
>>>>>>> Developer
    }

    @Override
    public ApiResponse<String> changePassword(ChangePasswordRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Customer customer = customerRepository
                .findByEmail(email)
                .orElseThrow(() ->
<<<<<<< HEAD
                        new RuntimeException("Customer not found"));
=======
                        new ResourceNotFoundException("Customer not found"));
>>>>>>> Developer

        // Verify old password
        if (!passwordEncoder.matches(
                request.getOldPassword(),
                customer.getPasswordHash())) {

<<<<<<< HEAD
            throw new RuntimeException("Old password is incorrect");
=======
            throw new BusinessException("Old password is incorrect");
>>>>>>> Developer
        }

        // Verify new password & confirm password
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "New password and Confirm password do not match");
        }

        // Prevent using the same password again
        if (passwordEncoder.matches(
                request.getNewPassword(),
                customer.getPasswordHash())) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
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

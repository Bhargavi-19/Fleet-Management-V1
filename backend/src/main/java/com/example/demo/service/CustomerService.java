package com.example.demo.service;

import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.UpdateCustomerRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.ProfileResponse;

public interface CustomerService {

<<<<<<< HEAD
    ApiResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    ApiResponse<ProfileResponse> updateProfile(UpdateCustomerRequest request);
    
    ProfileResponse getProfile();
=======
    ApiResponse<String> register(RegisterRequest request);

    ApiResponse<LoginResponse> login(LoginRequest request);

    ApiResponse<ProfileResponse> updateProfile(UpdateCustomerRequest request);

    ApiResponse<ProfileResponse> getProfile();
>>>>>>> Developer
    
    ApiResponse<String> changePassword(ChangePasswordRequest request);
      
}
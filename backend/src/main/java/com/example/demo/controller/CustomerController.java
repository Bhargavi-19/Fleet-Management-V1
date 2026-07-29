package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.UpdateCustomerRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.ProfileResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PostMapping("/register")
	public ApiResponse<String> register(
	        @Valid @RequestBody RegisterRequest request) {

		return customerService.register(request);
	}

	@PostMapping("/login")
	public ApiResponse<LoginResponse> login(
	        @Valid @RequestBody LoginRequest request) {

		return customerService.login(request);
	}

	@GetMapping("/profile")
	public ApiResponse<ProfileResponse> getProfile() {

	    return customerService.getProfile();
	}

	@PutMapping("/profile")
	public ApiResponse<ProfileResponse> updateProfile(
	        @Valid @RequestBody UpdateCustomerRequest request) {

	    return customerService.updateProfile(request);
	}

	@PutMapping("/change-password")
	public ApiResponse<String> changePassword(
	        @Valid @RequestBody ChangePasswordRequest request) {

	    return customerService.changePassword(request);
	}

}

package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterStaffRequest;
import com.example.demo.dto.request.UpdateStaffRequest;
import com.example.demo.dto.response.StaffLoginResponse;
import com.example.demo.dto.response.StaffResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.StaffService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    // Register Staff
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerStaff(
            @Valid @RequestBody RegisterStaffRequest request) {

        return ResponseEntity.ok(staffService.registerStaff(request));
    }

    // Staff Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<StaffLoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        StaffLoginResponse response = staffService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Login successful",
                        response
                )
        );
    }

    // Get Staff by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> getStaffById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    // Get Staff by Hub ID
    @GetMapping("/hub/{hubId}")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getStaffByHubId(
            @PathVariable Integer hubId) {

        return ResponseEntity.ok(staffService.getStaffByHubId(hubId));
    }

    // Get All Staff
    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getAllStaff() {

        return ResponseEntity.ok(staffService.getAllStaff());
    }

    // Update Staff
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> updateStaff(
            @PathVariable Integer id,
            @RequestBody UpdateStaffRequest request) {

        return ResponseEntity.ok(staffService.updateStaff(id, request));
    }

    // Delete Staff
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteStaff(
            @PathVariable Integer id) {

        return ResponseEntity.ok(staffService.deleteStaff(id));
    }
}
package com.example.demo.service;
import java.util.List;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterStaffRequest;
import com.example.demo.dto.request.UpdateStaffRequest;
import com.example.demo.dto.response.StaffLoginResponse;
import com.example.demo.dto.response.StaffResponse;
import com.example.demo.response.ApiResponse;

public interface StaffService {

    ApiResponse<String> registerStaff(RegisterStaffRequest request);

    StaffLoginResponse login(LoginRequest request);

    ApiResponse<StaffResponse> getStaffById(Integer id);

    ApiResponse<List<StaffResponse>> getStaffByHubId(Integer hubId);

    ApiResponse<List<StaffResponse>> getAllStaff();

    ApiResponse<StaffResponse> updateStaff(Integer id, UpdateStaffRequest request);

    ApiResponse<String> deleteStaff(Integer id);
}

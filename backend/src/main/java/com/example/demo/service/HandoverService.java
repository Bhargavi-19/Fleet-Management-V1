package com.example.demo.service;

import com.example.demo.dto.request.AssignVehicleRequest;
import com.example.demo.dto.request.ConfirmHandoverRequest;
import com.example.demo.dto.request.ReturnVehicleRequest;
import com.example.demo.dto.response.AssignVehicleResponse;
import com.example.demo.response.ApiResponse;

public interface HandoverService {

    ApiResponse<AssignVehicleResponse> assignVehicle(
            AssignVehicleRequest request);
    
    ApiResponse<String> confirmHandover(
            ConfirmHandoverRequest request);

    ApiResponse<String> returnVehicle(
            ReturnVehicleRequest request);
}
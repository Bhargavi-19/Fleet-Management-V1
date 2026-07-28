package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.request.AssignVehicleRequest;
import com.example.demo.dto.request.ConfirmHandoverRequest;
import com.example.demo.dto.request.ReturnVehicleRequest;
import com.example.demo.dto.response.AssignVehicleResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.HandoverService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/staff/handover")
public class StaffHandoverController {

    private final HandoverService handoverService;

    public StaffHandoverController(
            HandoverService handoverService) {

        this.handoverService = handoverService;
    }

    @PostMapping("/assign-vehicle")
    public ResponseEntity<ApiResponse<AssignVehicleResponse>>
    assignVehicle(

            @Valid
            @RequestBody
            AssignVehicleRequest request) {

        return ResponseEntity.ok(

                handoverService.assignVehicle(request));
    }
    
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<String>> confirmHandover(
            @Valid @RequestBody ConfirmHandoverRequest request) {

        ApiResponse<String> response =
                handoverService.confirmHandover(request);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/return")
    public ResponseEntity<ApiResponse<String>> returnVehicle(
            @Valid @RequestBody ReturnVehicleRequest request){

        return ResponseEntity.ok(
                handoverService.returnVehicle(request));
    }
}
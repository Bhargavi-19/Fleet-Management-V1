package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.response.CarTypeResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CarTypeService;

@RestController
@RequestMapping("/api/car-types")

public class CarTypeController {

    private final CarTypeService service;

    public CarTypeController(CarTypeService service) {
        this.service = service;
    }

    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<List<CarTypeResponse>>> getCarTypesByHubId(
            @PathVariable Integer hubId) {

        List<CarTypeResponse> carTypes = service.getCarTypesByHubId(hubId);

        ApiResponse<List<CarTypeResponse>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Car types fetched successfully.");
        response.setData(carTypes);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
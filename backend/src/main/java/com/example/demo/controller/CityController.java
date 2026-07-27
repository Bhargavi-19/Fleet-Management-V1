package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.response.CityResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CityService;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin(origins = "*")
public class CityController {

    private final CityService service;

    public CityController(CityService service) {
        this.service = service;
    }

    @GetMapping("/state/{stateId}")
    public ResponseEntity<ApiResponse<List<CityResponse>>> getCitiesByStateId(
            @PathVariable Integer stateId) {

        List<CityResponse> cities = service.getCitiesByStateId(stateId);

        ApiResponse<List<CityResponse>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Cities fetched successfully.");
        response.setData(cities);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
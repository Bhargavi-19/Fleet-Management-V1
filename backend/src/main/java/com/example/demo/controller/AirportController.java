package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.response.AirportResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.AirportService;

@RestController
@RequestMapping("/api/airports")

public class AirportController {

    private final AirportService service;

    public AirportController(AirportService service) {
        this.service = service;
    }

    // Get all airports
    @GetMapping
    public ResponseEntity<ApiResponse<List<AirportResponse>>> getAllAirports() {

        List<AirportResponse> airports = service.getAllAirports();

        ApiResponse<List<AirportResponse>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Airports fetched successfully.");
        response.setData(airports);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get airports by city
    @GetMapping("/city/{cityId}")
    public ResponseEntity<ApiResponse<List<AirportResponse>>> getAirportsByCityId(
            @PathVariable Integer cityId) {

        List<AirportResponse> airports = service.getAirportsByCityId(cityId);

        ApiResponse<List<AirportResponse>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Airports fetched successfully.");
        response.setData(airports);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Search airport
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AirportResponse>>> searchAirport(
            @RequestParam String keyword) {

        List<AirportResponse> airports = service.searchAirport(keyword);

        ApiResponse<List<AirportResponse>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Search completed successfully.");
        response.setData(airports);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<List<AirportResponse>>> getAirportsByHubId(
            @PathVariable Integer hubId) {

        List<AirportResponse> airports = service.getAirportsByHubId(hubId);

        ApiResponse<List<AirportResponse>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Airports fetched successfully.");
        response.setData(airports);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
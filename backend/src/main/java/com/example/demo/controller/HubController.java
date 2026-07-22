package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.response.HubResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.HubService;

@RestController
@RequestMapping("/api/hubs")

public class HubController {

    private final HubService service;

    public HubController(HubService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HubResponse>>> getAllHubs(
            @RequestParam Integer stateId,
            @RequestParam Integer cityId) {

        List<HubResponse> hubs = service.getAllHubs(stateId, cityId);

        ApiResponse<List<HubResponse>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Hubs fetched successfully.");
        response.setData(hubs);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubResponse>> getHubById(
            @PathVariable Integer hubId) {

        HubResponse hub = service.getHubById(hubId);

        ApiResponse<HubResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Hub fetched successfully.");
        response.setData(hub);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
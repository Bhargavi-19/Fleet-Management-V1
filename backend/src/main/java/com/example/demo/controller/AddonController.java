package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.response.AddonResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.AddonService;

@RestController
@RequestMapping("/api/addons")

public class AddonController {

    private final AddonService service;

    public AddonController(AddonService service) {
        this.service = service;
    }

    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<List<AddonResponse>>> getAddonsByHubId(
            @PathVariable Integer hubId) {

        List<AddonResponse> addons = service.getAddonsByHubId(hubId);

        ApiResponse<List<AddonResponse>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Add-ons fetched successfully.");
        response.setData(addons);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
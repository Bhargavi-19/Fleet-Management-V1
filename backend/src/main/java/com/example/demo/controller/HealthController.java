package com.example.demo.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.response.ApiResponse;

@RestController
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {

        ApiResponse<String> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Application is running successfully.");
        response.setData("OK");

        return ResponseEntity.ok(response);
    }
}

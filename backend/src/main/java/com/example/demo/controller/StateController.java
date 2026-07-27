package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.response.StateResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.StateService;

@RestController
@RequestMapping("/api/states")

public class StateController {

    private final StateService service;

    public StateController(StateService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StateResponse>>> getAllStates() {

        List<StateResponse> states = service.getAllStates();

        ApiResponse<List<StateResponse>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("States fetched successfully.");
        response.setData(states);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
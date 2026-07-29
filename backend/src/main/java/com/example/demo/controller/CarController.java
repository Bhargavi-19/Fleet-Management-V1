package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.response.CarResponse;
import com.example.demo.enums.CarStatus;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CarService;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CarResponse>>> getAllCars() {

        List<CarResponse> cars = service.getAllCars();

        ApiResponse<List<CarResponse>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Cars fetched successfully.");
        response.setData(cars);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{carId}")
    public ResponseEntity<ApiResponse<CarResponse>> getCarById(
            @PathVariable Integer carId) {

        CarResponse car = service.getCarById(carId);

        ApiResponse<CarResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Car fetched successfully.");
        response.setData(car);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Cars belonging to a hub.
     *
     * Pass ?status=AVAILABLE to get only the vehicles staff are allowed to
     * assign to a booking.
     */
    @GetMapping("/hub/{hubId}")
    public ResponseEntity<ApiResponse<List<CarResponse>>> getCarsByHub(
            @PathVariable Integer hubId,
            @RequestParam(required = false) CarStatus status) {

        List<CarResponse> cars = service.getCarsByHub(hubId, status);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cars fetched successfully.", cars));
    }

    /**
     * Cars of one category at a hub.
     * Pass ?status=AVAILABLE to get only the vehicles staff may hand over.
     */
    @GetMapping("/hub/{hubId}/car-type/{carTypeId}")
    public ResponseEntity<ApiResponse<List<CarResponse>>> getCarsByHubAndCarType(
            @PathVariable Integer hubId,
            @PathVariable Integer carTypeId,
            @RequestParam(required = false) CarStatus status) {

        List<CarResponse> cars =
                service.getCarsByHubAndCarType(hubId, carTypeId, status);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cars fetched successfully.", cars));
    }
}
package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.response.CarResponse;
import com.example.demo.enums.CarStatus;

public interface CarService {

    List<CarResponse> getAllCars();

    CarResponse getCarById(Integer carId);

    /** Cars of a hub, optionally filtered by status (null = all). */
    List<CarResponse> getCarsByHub(Integer hubId, CarStatus status);

    /** Cars of one category at a hub, optionally filtered by status. */
    List<CarResponse> getCarsByHubAndCarType(
            Integer hubId, Integer carTypeId, CarStatus status);

}
package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.response.CarResponse;
<<<<<<< HEAD
=======
import com.example.demo.enums.CarStatus;
>>>>>>> Developer

public interface CarService {

    List<CarResponse> getAllCars();

    CarResponse getCarById(Integer carId);
<<<<<<< HEAD
    
    List<CarResponse> getCarsByHub(Integer hubId);

    List<CarResponse> getCarsByHubAndCarType(Integer hubId, Integer carTypeId);
=======

    /** Cars of a hub, optionally filtered by status (null = all). */
    List<CarResponse> getCarsByHub(Integer hubId, CarStatus status);

    /** Cars of one category at a hub, optionally filtered by status. */
    List<CarResponse> getCarsByHubAndCarType(
            Integer hubId, Integer carTypeId, CarStatus status);
>>>>>>> Developer

}
package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.response.CarResponse;

public interface CarService {

    List<CarResponse> getAllCars();

    CarResponse getCarById(Integer carId);

}
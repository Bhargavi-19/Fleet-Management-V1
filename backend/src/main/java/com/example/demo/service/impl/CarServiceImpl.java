package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.CarResponse;
import com.example.demo.entity.base.Car;
import com.example.demo.exception.error.ResourceNotFoundException;
import com.example.demo.repository.CarRepository;
import com.example.demo.service.CarService;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository repository;

    public CarServiceImpl(CarRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CarResponse> getAllCars() {

        List<Car> cars = repository.findAll();

        if (cars.isEmpty()) {
            throw new ResourceNotFoundException("No cars found.");
        }

        return cars.stream()
                .map(CarResponse::fromEntity)
                .toList();
    }

    @Override
    public CarResponse getCarById(Integer carId) {

        Car car = repository.findById(carId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Car not found with Id : " + carId));

        return CarResponse.fromEntity(car);
    }
    
    @Override
    public List<CarResponse> getCarsByHub(Integer hubId) {

        return repository.findByHub_HubId(hubId)
                .stream()
                .map(CarResponse::fromEntity)
                .toList();
    }

    @Override
    public List<CarResponse> getCarsByHubAndCarType(Integer hubId, Integer carTypeId) {

        return repository.findByHub_HubIdAndCarType_CarTypeId(hubId, carTypeId)
                .stream()
                .map(CarResponse::fromEntity)
                .toList();
    }
}
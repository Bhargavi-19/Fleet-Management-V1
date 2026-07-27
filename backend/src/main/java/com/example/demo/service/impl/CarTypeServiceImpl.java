package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.CarStatusCountResponse;
import com.example.demo.dto.response.CarTypeResponse;
import com.example.demo.entity.base.CarType;
import com.example.demo.enums.CarStatus;
import com.example.demo.exception.error.ResourceNotFoundException;
import com.example.demo.repository.CarTypeRepository;
import com.example.demo.service.CarTypeService;

@Service
public class CarTypeServiceImpl implements CarTypeService {

    private final CarTypeRepository repository;

    public CarTypeServiceImpl(CarTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CarTypeResponse> getCarTypesByHubId(Integer hubId) {

        List<CarType> carTypes = repository.findByHub_HubId(hubId);

        if (carTypes.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No car types found for Hub Id : " + hubId);
        }

        return carTypes.stream()
                .map(CarTypeResponse::fromEntity)
                .toList();
    }
    
    @Override
    public List<CarStatusCountResponse> getCarCountByStatus() {

        List<Object[]> results = repository.countCarsByStatus();

        List<CarStatusCountResponse> response = new ArrayList<>();

        for (Object[] row : results) {

            CarStatus status = (CarStatus) row[0];
            Long count = ((Number) row[1]).longValue();

            response.add(new CarStatusCountResponse(status, count));
        }

        return response;
    }
}
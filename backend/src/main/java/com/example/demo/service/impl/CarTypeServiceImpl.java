package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.CarTypeResponse;
import com.example.demo.entity.base.CarType;
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
	
	    List<CarType> carTypes = repository.findByHubId(hubId);

        if (carTypes.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No car types found for Hub Id : " + hubId);
        }

        return carTypes.stream()
                .map(CarTypeResponse::fromEntity)
                .toList();
	}

}
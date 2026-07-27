package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.CityResponse;
import com.example.demo.entity.base.City;
import com.example.demo.exception.error.ResourceNotFoundException;
import com.example.demo.repository.CityRepository;
import com.example.demo.service.CityService;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository repository;

    public CityServiceImpl(CityRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CityResponse> getCitiesByStateId(Integer stateId) {

        List<City> cities = repository.findByState_StateId(stateId);

        if (cities.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No cities found for State Id : " + stateId);
        }

        return cities.stream()
                .map(CityResponse::fromEntity)
                .toList();
    }
}
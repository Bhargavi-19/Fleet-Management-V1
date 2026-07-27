package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.AirportResponse;
import com.example.demo.entity.base.Airport;
import com.example.demo.exception.error.ResourceNotFoundException;
import com.example.demo.repository.AirportRepository;
import com.example.demo.service.AirportService;

@Service
public class AirportServiceImpl implements AirportService {

    private final AirportRepository repository;

    public AirportServiceImpl(AirportRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AirportResponse> getAllAirports() {

        List<Airport> airports = repository.findAll();

        if (airports.isEmpty()) {
            throw new ResourceNotFoundException("No airports found.");
        }

        return airports.stream()
                .map(AirportResponse::fromEntity)
                .toList();
    }

    @Override
    public List<AirportResponse> getAirportsByCityId(Integer cityId) {

        List<Airport> airports = repository.findByCity_CityId(cityId);

        if (airports.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No airports found for City Id : " + cityId);
        }

        return airports.stream()
                .map(AirportResponse::fromEntity)
                .toList();
    }

    @Override
    public List<AirportResponse> searchAirport(String keyword) {

        List<Airport> airports = repository.searchAirport(keyword);

        if (airports.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No airports found for keyword : " + keyword);
        }

        return airports.stream()
                .map(AirportResponse::fromEntity)
                .toList();
    }
    
    @Override
    public List<AirportResponse> getAirportsByHubId(Integer hubId) {

        List<Airport> airports = repository.findByHub_HubId(hubId);

        if (airports.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No airports found for hub ID : " + hubId);
        }

        return airports.stream()
                .map(AirportResponse::fromEntity)
                .toList();
    }
    
}
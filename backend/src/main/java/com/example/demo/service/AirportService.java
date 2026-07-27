package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.response.AirportResponse;
import com.example.demo.entity.base.Airport;

public interface AirportService {

    List<AirportResponse> getAllAirports();

    List<AirportResponse> getAirportsByCityId(Integer cityId);

    List<AirportResponse> searchAirport(String keyword);

}
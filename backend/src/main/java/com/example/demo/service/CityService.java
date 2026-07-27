package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.response.CityResponse;
import com.example.demo.entity.base.City;

public interface CityService {

    List<CityResponse> getCitiesByStateId(Integer stateId);

}
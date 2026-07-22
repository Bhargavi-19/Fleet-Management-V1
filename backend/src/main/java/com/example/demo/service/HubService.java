package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.response.HubResponse;

public interface HubService {

    HubResponse getHubById(Integer hubId);

    List<HubResponse> getAllHubs(Integer stateId, Integer cityId);

}
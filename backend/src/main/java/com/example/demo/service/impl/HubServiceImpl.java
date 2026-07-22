package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.HubResponse;
import com.example.demo.entity.base.Hub;
import com.example.demo.exception.error.ResourceNotFoundException;
import com.example.demo.repository.HubRepository;
import com.example.demo.service.HubService;

@Service
public class HubServiceImpl implements HubService {

    private final HubRepository repository;

    public HubServiceImpl(HubRepository repository) {
        this.repository = repository;
    }

    @Override
    public HubResponse getHubById(Integer hubId) {

        Hub hub = repository.findByHubId(hubId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Hub not found with Id : " + hubId));

        return HubResponse.fromEntity(hub);
    }

    @Override
    public List<HubResponse> getAllHubs(Integer stateId, Integer cityId) {

        List<Hub> hubs = repository.findByStateIdAndCityId(stateId, cityId);

        if (hubs.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No hubs found for given State and City.");
        }

        return hubs.stream()
                .map(HubResponse::fromEntity)
                .toList();
    }
}
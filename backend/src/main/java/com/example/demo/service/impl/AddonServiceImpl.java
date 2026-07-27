package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.AddonResponse;
import com.example.demo.entity.base.Addon;
import com.example.demo.exception.error.ResourceNotFoundException;
import com.example.demo.repository.AddonRepository;
import com.example.demo.service.AddonService;

@Service
public class AddonServiceImpl implements AddonService {

    private final AddonRepository repository;

    public AddonServiceImpl(AddonRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AddonResponse> getAddonsByHubId(Integer hubId) {

        List<Addon> addons = repository.findByHub_HubId(hubId);

        if (addons.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No Add-ons found for Hub Id : " + hubId);
        }

        return addons.stream()
                     .map(AddonResponse::fromEntity)
                     .toList();
    }
}
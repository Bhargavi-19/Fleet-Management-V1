package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.response.AddonResponse;

public interface AddonService {

    List<AddonResponse> getAddonsByHubId(Integer hubId);

}

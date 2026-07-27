package com.example.demo.dto.response;

import com.example.demo.entity.base.Addon;

public class AddonResponse {

    private Integer addonId;
    private String addonName;
    private String description;
    private Double pricePerDay;

    // Default Constructor
    public AddonResponse() {
    }

    // Parameterized Constructor
    public AddonResponse(Integer addonId, String addonName,
                         String description, Double pricePerDay) {
        this.addonId = addonId;
        this.addonName = addonName;
        this.description = description;
        this.pricePerDay = pricePerDay;
    }

    // Convert Entity to DTO
    public static AddonResponse fromEntity(Addon addon) {

        AddonResponse response = new AddonResponse();

        response.setAddonId(addon.getAddonId());
        response.setAddonName(addon.getAddonName());
        response.setDescription(addon.getDescription());
        response.setPricePerDay(addon.getPricePerDay());

        return response;
    }

    public Integer getAddonId() {
        return addonId;
    }

    public void setAddonId(Integer addonId) {
        this.addonId = addonId;
    }

    public String getAddonName() {
        return addonName;
    }

    public void setAddonName(String addonName) {
        this.addonName = addonName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }
}
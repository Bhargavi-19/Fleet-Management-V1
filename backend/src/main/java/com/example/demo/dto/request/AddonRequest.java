package com.example.demo.dto.request;

import java.math.BigDecimal;

public class AddonRequest {

    private Long addonId;
    private String addonName;
    private BigDecimal addonPrice;

    public AddonRequest() {
    }

    public Long getAddonId() {
        return addonId;
    }

    public void setAddonId(Long addonId) {
        this.addonId = addonId;
    }

    public String getAddonName() {
        return addonName;
    }

    public void setAddonName(String addonName) {
        this.addonName = addonName;
    }

    public BigDecimal getAddonPrice() {
        return addonPrice;
    }

    public void setAddonPrice(BigDecimal addonPrice) {
        this.addonPrice = addonPrice;
    }
}
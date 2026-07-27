package com.example.demo.dto.request;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

public class UpdateBookingRequest {

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer pickupHubId;
    private String pickupHubName;

    private Integer dropoffHubId;
    private String dropoffHubName;

    private Integer duration;

    private BigDecimal vehicleAmount;
    private BigDecimal addonAmount;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;

    private List<AddonRequest> addons;

    public UpdateBookingRequest() {
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getPickupHubId() {
        return pickupHubId;
    }

    public void setPickupHubId(Integer pickupHubId) {
        this.pickupHubId = pickupHubId;
    }

    public String getPickupHubName() {
        return pickupHubName;
    }

    public void setPickupHubName(String pickupHubName) {
        this.pickupHubName = pickupHubName;
    }

    public Integer getDropoffHubId() {
        return dropoffHubId;
    }

    public void setDropoffHubId(Integer dropoffHubId) {
        this.dropoffHubId = dropoffHubId;
    }

    public String getDropoffHubName() {
        return dropoffHubName;
    }

    public void setDropoffHubName(String dropoffHubName) {
        this.dropoffHubName = dropoffHubName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public BigDecimal getVehicleAmount() {
        return vehicleAmount;
    }

    public void setVehicleAmount(BigDecimal vehicleAmount) {
        this.vehicleAmount = vehicleAmount;
    }

    public BigDecimal getAddonAmount() {
        return addonAmount;
    }

    public void setAddonAmount(BigDecimal addonAmount) {
        this.addonAmount = addonAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public List<AddonRequest> getAddons() {
        return addons;
    }

    public void setAddons(List<AddonRequest> addons) {
        this.addons = addons;
    }
}
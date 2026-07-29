package com.example.demo.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BookingRequest {

    // The customer books a car TYPE. The actual car is assigned by
    // staff at hand-over, so carId is normally left empty here.
    private Long carId;

    @NotNull(message = "Car type is required")
    private Long carTypeId;

    // Captured on the "Your Info" step. When supplied these are copied onto
    // the customer's profile, so they never have to type them again.
    private String drivingLicenseNo;
    private String passportNo;

    // The billing address, also captured on the "Your Info" step.
    //
    // WHY IT LIVES ON THE BOOKING REQUEST
    // -----------------------------------
    // The booking snapshot copies the address off the Customer record, and
    // the invoice later prints that snapshot. If we only accepted the address
    // on the Update Profile screen, anyone who booked without visiting that
    // screen first would end up with a blank address on their invoice.
    // Accepting it here means the booking form is enough on its own.
    //
    // All five are optional: a returning customer already has an address
    // saved, so the form simply sends back what it pre-filled.
    private String addressLine1;
    private String addressLine2;
    private Integer stateId;
    private Integer cityId;
    private String pincode;

    /** A booking can never start in the past. */
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "The pick-up date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Pick-up hub is required")
    private Integer pickupHubId;

    private String pickupHubName;

    @NotNull(message = "Drop-off hub is required")
    private Integer dropoffHubId;

    private String dropoffHubName;

    private BigDecimal dailyRate;
    private BigDecimal weeklyRate;
    private BigDecimal monthlyRate;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer duration;

    private BigDecimal vehicleAmount;
    private BigDecimal addonAmount;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;

    private List<AddonRequest> addons;

    public BookingRequest() {
    }

    public String getDrivingLicenseNo() {
        return drivingLicenseNo;
    }

    public void setDrivingLicenseNo(String drivingLicenseNo) {
        this.drivingLicenseNo = drivingLicenseNo;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(Long carTypeId) {
        this.carTypeId = carTypeId;
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

    public String getPickupHubName() {
        return pickupHubName;
    }

    public void setPickupHubName(String pickupHubName) {
        this.pickupHubName = pickupHubName;
    }

    public String getDropoffHubName() {
        return dropoffHubName;
    }

    public void setDropoffHubName(String dropoffHubName) {
        this.dropoffHubName = dropoffHubName;
    }
    
    public Integer getPickupHubId() {
		return pickupHubId;
	}

	public void setPickupHubId(Integer pickupHubId) {
		this.pickupHubId = pickupHubId;
	}

	public Integer getDropoffHubId() {
		return dropoffHubId;
	}

	public void setDropoffHubId(Integer dropoffHubId) {
		this.dropoffHubId = dropoffHubId;
	}

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public BigDecimal getWeeklyRate() {
        return weeklyRate;
    }

    public void setWeeklyRate(BigDecimal weeklyRate) {
        this.weeklyRate = weeklyRate;
    }

    public BigDecimal getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(BigDecimal monthlyRate) {
        this.monthlyRate = monthlyRate;
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
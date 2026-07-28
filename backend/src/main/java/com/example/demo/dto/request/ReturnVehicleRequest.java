package com.example.demo.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ReturnVehicleRequest {
    
    @NotNull(message = "Booking Id is required")
    private Long bookingId;

    @NotNull(message = "Fuel level is required")
    @Min(0)
    @Max(100)
    private Integer fuelLevelIn;

    @NotNull(message = "Fuel charges are required")
    @DecimalMin(value = "0.0")
    private BigDecimal fuelCharges;

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public Integer getFuelLevelIn() {
		return fuelLevelIn;
	}

	public void setFuelLevelIn(Integer fuelLevelIn) {
		this.fuelLevelIn = fuelLevelIn;
	}

	public BigDecimal getFuelCharges() {
		return fuelCharges;
	}

	public void setFuelCharges(BigDecimal fuelCharges) {
		this.fuelCharges = fuelCharges;
	}



    // Getters & Setters
    
    
}

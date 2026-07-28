package com.example.demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ConfirmHandoverRequest {

    @NotNull(message = "Booking Id is required")
    private Long bookingId;

    @NotNull(message = "Fuel level is required")
    @Min(value = 0, message = "Fuel level cannot be less than 0")
    @Max(value = 100, message = "Fuel level cannot be greater than 100")
    private Integer fuelLevelOut;

    public ConfirmHandoverRequest() {
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getFuelLevelOut() {
        return fuelLevelOut;
    }

    public void setFuelLevelOut(Integer fuelLevelOut) {
        this.fuelLevelOut = fuelLevelOut;
    }
}
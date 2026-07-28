package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;

public class AssignVehicleRequest {

    @NotNull(message = "Booking Id is required")
    private Long bookingId;

    @NotNull(message = "Car Id is required")
    private Integer carId;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }
}
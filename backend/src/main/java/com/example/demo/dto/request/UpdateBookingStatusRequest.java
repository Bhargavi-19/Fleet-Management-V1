package com.example.demo.dto.request;

import com.example.demo.enums.BookingStatus;

<<<<<<< HEAD
public class UpdateBookingStatusRequest {

=======
import jakarta.validation.constraints.NotNull;

public class UpdateBookingStatusRequest {

    @NotNull(message = "Booking status is required")
>>>>>>> Developer
    private BookingStatus status;

    public UpdateBookingStatusRequest() {
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
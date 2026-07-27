package com.example.demo.dto.request;

import com.example.demo.enums.BookingStatus;

public class UpdateBookingStatusRequest {

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
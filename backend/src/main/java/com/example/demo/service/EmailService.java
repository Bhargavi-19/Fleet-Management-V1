package com.example.demo.service;

import com.example.demo.dto.response.BookingResponse;

public interface EmailService {

    void sendBookingConfirmation(
            BookingResponse booking);
}
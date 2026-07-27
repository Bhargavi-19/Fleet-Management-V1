package com.example.demo.service;

import com.example.demo.dto.response.BookingResponse;

public interface PdfService {

    byte[] generateBookingPdf(
            BookingResponse booking);
}
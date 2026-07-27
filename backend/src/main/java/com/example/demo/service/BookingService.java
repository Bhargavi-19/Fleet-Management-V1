package com.example.demo.service;

import java.util.Optional;

import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.request.GuestBookingRequest;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.entity.base.Customer;
import com.example.demo.response.ApiResponse;

public interface BookingService {

    ApiResponse<BookingResponse> createBooking(BookingRequest request);
    
    ApiResponse<BookingResponse> createGuestBooking(GuestBookingRequest request);
    
}
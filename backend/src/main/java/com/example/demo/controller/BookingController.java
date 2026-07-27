package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.request.GuestBookingRequest;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @RequestBody BookingRequest request) {

        ApiResponse<BookingResponse> response =
                bookingService.createBooking(request);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/guest")
    public ResponseEntity<ApiResponse<BookingResponse>> createGuestBooking(
            @RequestBody GuestBookingRequest request) {

        ApiResponse<BookingResponse> response =
                bookingService.createGuestBooking(request);

        return ResponseEntity.ok(response);
    }
}
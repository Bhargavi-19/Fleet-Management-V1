package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.request.GuestBookingRequest;
import com.example.demo.dto.request.UpdateBookingRequest;
import com.example.demo.dto.request.UpdateBookingStatusRequest;
import com.example.demo.dto.response.BookingPageResponse;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.dto.response.BookingStatsResponse;
import com.example.demo.enums.BookingStatus;
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
    
    @GetMapping
    public ResponseEntity<ApiResponse<BookingPageResponse>> getBookings(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            BookingStatus status) {

        ApiResponse<BookingPageResponse> response =
                bookingService.getBookings(
                        page,
                        size,
                        status);

        return ResponseEntity.ok(response);
    }
    
    
    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>>
            getBookingById(
                    @PathVariable Long bookingId) {

        ApiResponse<BookingResponse> response =
                bookingService.getBookingById(
                        bookingId);

        return ResponseEntity.ok(response);
    }
    
    
    @PutMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>>
            updateBooking(
                    @PathVariable Long bookingId,
                    @RequestBody UpdateBookingRequest request) {

        ApiResponse<BookingResponse> response =
                bookingService.updateBooking(
                        bookingId,
                        request);

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>>
            cancelBooking(
                    @PathVariable Long bookingId) {

        ApiResponse<BookingResponse> response =
                bookingService.cancelBooking(
                        bookingId);

        return ResponseEntity.ok(response);
    }
    
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<BookingStatsResponse>>
            getBookingStats() {

        ApiResponse<BookingStatsResponse> response =
                bookingService.getBookingStats();

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<ApiResponse<BookingResponse>>
            updateBookingStatus(
                    @PathVariable Long bookingId,
                    @RequestBody UpdateBookingStatusRequest request) {

        ApiResponse<BookingResponse> response =
                bookingService.updateBookingStatus(
                        bookingId,
                        request);

        return ResponseEntity.ok(response);
    }
}



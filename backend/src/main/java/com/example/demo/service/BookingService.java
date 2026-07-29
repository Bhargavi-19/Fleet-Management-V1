package com.example.demo.service;

import java.util.Optional;

import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.request.GuestBookingRequest;
import com.example.demo.dto.request.UpdateBookingRequest;
import com.example.demo.dto.request.UpdateBookingStatusRequest;
import com.example.demo.dto.response.BookingPageResponse;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.dto.response.BookingStatsResponse;
import com.example.demo.entity.base.Customer;
import com.example.demo.enums.BookingStatus;
import com.example.demo.enums.HubScope;
import com.example.demo.response.ApiResponse;

public interface BookingService {

    ApiResponse<BookingResponse> createBooking(BookingRequest request);
    
    ApiResponse<BookingResponse> createGuestBooking(GuestBookingRequest request);
    
    ApiResponse<BookingPageResponse> getBookings(
            int page,
            int size,
            BookingStatus status);
    
    ApiResponse<BookingResponse> getBookingById(
            Long bookingId);
    
    ApiResponse<BookingResponse> updateBooking(
            Long bookingId,
            UpdateBookingRequest request);
    
    ApiResponse<BookingResponse> cancelBooking(
            Long bookingId);
    
    
    ApiResponse<BookingStatsResponse> getBookingStats();
    
    
    /**
     * Staff bookings, scoped to which end of the rental this hub handles.
     * @param scope PICKUP (hand-over), RETURN, or ALL. Null means ALL.
     */
    ApiResponse<BookingPageResponse> getStaffBookings(
            int page,
            int size,
            BookingStatus status,
            HubScope scope);
    
    ApiResponse<BookingResponse> getStaffBookingById(
            Long bookingId);
    
    ApiResponse<BookingStatsResponse> getStaffBookingStats();
    
    ApiResponse<BookingResponse> updateStaffBookingStatus(
            Long bookingId,
            UpdateBookingStatusRequest request);
    
    ApiResponse<BookingResponse> cancelStaffBooking(
            Long bookingId);
    
    
}
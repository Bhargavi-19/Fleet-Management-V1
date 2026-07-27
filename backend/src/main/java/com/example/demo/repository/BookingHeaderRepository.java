package com.example.demo.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.BookingHeader;
import com.example.demo.enums.BookingStatus;

public interface BookingHeaderRepository
        extends JpaRepository<BookingHeader, Long> {

    // =====================================================
    // CUSTOMER - Pagination
    // =====================================================

    // Customer's all bookings
    Page<BookingHeader> findByCustomerId(
            String customerId,
            Pageable pageable);

    // Customer's bookings filtered by status
    Page<BookingHeader> findByCustomerIdAndBookingStatus(
            String customerId,
            BookingStatus bookingStatus,
            Pageable pageable);


    // =====================================================
    // STAFF - Hub Pagination
    // =====================================================

    // All bookings of staff's pickup hub
    Page<BookingHeader> findByPickupHubId(
            Integer pickupHubId,
            Pageable pageable);

    // Hub bookings filtered by status
    Page<BookingHeader> findByPickupHubIdAndBookingStatus(
            Integer pickupHubId,
            BookingStatus bookingStatus,
            Pageable pageable);


    // =====================================================
    // CUSTOMER - Counts
    // =====================================================

    long countByCustomerId(
            String customerId);

    long countByCustomerIdAndBookingStatus(
            String customerId,
            BookingStatus bookingStatus);


    // =====================================================
    // STAFF - Hub Counts
    // =====================================================

    long countByPickupHubId(
            Integer pickupHubId);

    long countByPickupHubIdAndBookingStatus(
            Integer pickupHubId,
            BookingStatus bookingStatus);


    // =====================================================
    // STAFF - Today's booking count
    // =====================================================

    long countByPickupHubIdAndCreatedAtBetween(
            Integer pickupHubId,
            LocalDateTime start,
            LocalDateTime end);
}
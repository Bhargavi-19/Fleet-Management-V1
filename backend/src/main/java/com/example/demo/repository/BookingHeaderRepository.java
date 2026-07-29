package com.example.demo.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
=======
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
>>>>>>> Developer

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
<<<<<<< HEAD
}
=======

    // =====================================================
    // STAFF - drop-off hub (the Return module)
    //
    // A one-way rental is collected at one hub and returned to another, so
    // the Return module must look at dropoff_hub_id, not pickup_hub_id.
    // =====================================================

    Page<BookingHeader> findByDropoffHubId(
            Integer dropoffHubId,
            Pageable pageable);

    Page<BookingHeader> findByDropoffHubIdAndBookingStatus(
            Integer dropoffHubId,
            BookingStatus bookingStatus,
            Pageable pageable);

    long countByDropoffHubId(Integer dropoffHubId);

    long countByDropoffHubIdAndBookingStatus(
            Integer dropoffHubId,
            BookingStatus bookingStatus);

    // =====================================================
    // STAFF - everything the hub is involved in, either end
    // =====================================================

    @Query("""
            SELECT b FROM BookingHeader b
            WHERE (b.pickupHubId = :hubId OR b.dropoffHubId = :hubId)
              AND (:status IS NULL OR b.bookingStatus = :status)
            """)
    Page<BookingHeader> findByEitherHub(
            @Param("hubId") Integer hubId,
            @Param("status") BookingStatus status,
            Pageable pageable);

    @Query("""
            SELECT COUNT(b) FROM BookingHeader b
            WHERE (b.pickupHubId = :hubId OR b.dropoffHubId = :hubId)
              AND (:status IS NULL OR b.bookingStatus = :status)
            """)
    long countByEitherHub(
            @Param("hubId") Integer hubId,
            @Param("status") BookingStatus status);
}
>>>>>>> Developer

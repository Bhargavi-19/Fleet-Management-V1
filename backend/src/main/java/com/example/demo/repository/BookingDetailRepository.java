package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.BookingDetail;

public interface BookingDetailRepository
        extends JpaRepository<BookingDetail, Long> {

    List<BookingDetail> findByBookingId(Long bookingId);
}
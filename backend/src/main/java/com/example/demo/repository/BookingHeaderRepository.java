package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.BookingHeader;

public interface BookingHeaderRepository
        extends JpaRepository<BookingHeader, Long> {

    List<BookingHeader> findByCustomerId(String customerId);
}
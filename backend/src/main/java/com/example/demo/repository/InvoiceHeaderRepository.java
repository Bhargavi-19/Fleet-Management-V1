package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.InvoiceHeader;

@Repository
public interface InvoiceHeaderRepository extends JpaRepository<InvoiceHeader, Long> {

    Optional<InvoiceHeader> findByBookingId(Long bookingId);

}
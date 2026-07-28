package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.base.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

    // Login
    Optional<Staff> findByEmail(String email);

    // Check duplicate email while registering
    boolean existsByEmail(String email);

    // Check duplicate phone number
    boolean existsByPhone(String phone);

    // Find all staff of a Hub
    List<Staff> findByHub_HubId(Integer hubId);

}
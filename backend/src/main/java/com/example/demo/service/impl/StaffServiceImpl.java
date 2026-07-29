package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.exception.error.BusinessException;
import com.example.demo.exception.error.ResourceNotFoundException;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterStaffRequest;
import com.example.demo.dto.request.UpdateStaffRequest;
import com.example.demo.dto.response.StaffLoginResponse;
import com.example.demo.dto.response.StaffResponse;
import com.example.demo.entity.base.Hub;
import com.example.demo.entity.base.Staff;
import com.example.demo.repository.HubRepository;
import com.example.demo.repository.StaffRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.StaffService;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private HubRepository hubRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public ApiResponse<String> registerStaff(RegisterStaffRequest request) {

        if (staffRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse<>(false, "Email already exists", null);
        }

        if (staffRepository.existsByPhone(request.getPhone())) {
            return new ApiResponse<>(false, "Phone number already exists", null);
        }

        Hub hub = hubRepository.findById(request.getHubId())
                .orElseThrow(() -> new ResourceNotFoundException("Hub not found"));

        Staff staff = new Staff();

        staff.setFirstName(request.getFirstName());
        staff.setLastName(request.getLastName());
        staff.setEmail(request.getEmail());
        staff.setGender(request.getGender());
        staff.setDateOfBirth(request.getDateOfBirth());
        staff.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        staff.setPhone(request.getPhone());
        staff.setAddressLine1(request.getAddressLine1());
        staff.setAddressLine2(request.getAddressLine2());
        staff.setCityName(request.getCityName());
        staff.setStateName(request.getStateName());
        staff.setHub(hub);

        staffRepository.save(staff);

        return new ApiResponse<>(
                true,
                "Staff registered successfully",
                null
        );
    }

    @Override
    public StaffLoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.getEmail(),
                        request.getPassword()
                )
        );

        Staff staff = staffRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Staff not found"));

        String token = jwtService.generateToken(staff.getEmail());

        return new StaffLoginResponse(

                token,
                "Bearer",
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getEmail(),
                staff.getHub().getHubId()

        );
    }
    
    private Staff getLoggedInStaff() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return staffRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Logged in staff not found"));
    }
    
    @Override
    public ApiResponse<StaffResponse> getStaffById(Integer id) {

        // Validate JWT and fetch logged-in staff
        Staff loggedInStaff = getLoggedInStaff();

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Staff not found"));

        return new ApiResponse<>(
                true,
                "Staff fetched successfully",
                StaffResponse.fromEntity(staff)
        );
    }
    
    @Override
    public ApiResponse<List<StaffResponse>> getStaffByHubId(Integer hubId) {

        Staff loggedInStaff = getLoggedInStaff();

        List<StaffResponse> staffList = staffRepository
                .findByHub_HubId(hubId)
                .stream()
                .map(StaffResponse::fromEntity)
                .toList();

        return new ApiResponse<>(
                true,
                "Staff list fetched successfully",
                staffList
        );
    }
    
    @Override
    public ApiResponse<List<StaffResponse>> getAllStaff() {

        Staff loggedInStaff = getLoggedInStaff();

        List<StaffResponse> staffList = staffRepository
                .findAll()
                .stream()
                .map(StaffResponse::fromEntity)
                .toList();

        return new ApiResponse<>(
                true,
                "All staff fetched successfully",
                staffList
        );
    }
    
    @Override
    public ApiResponse<String> deleteStaff(Integer id) {

        Staff loggedInStaff = getLoggedInStaff();

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Staff not found"));

        staffRepository.delete(staff);

        return new ApiResponse<>(
                true,
                "Staff deleted successfully",
                null
        );
    }

    @Override
    public ApiResponse<StaffResponse> updateStaff(
            Integer id,
            UpdateStaffRequest request) {

        Staff loggedInStaff = getLoggedInStaff();

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Staff not found"));

        if (!staff.getPhone().equals(request.getPhone())
                && staffRepository.existsByPhone(request.getPhone())) {

            throw new BusinessException("Phone number already exists");
        }

        Hub hub = hubRepository.findById(request.getHubId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hub not found"));

        staff.setFirstName(request.getFirstName());
        staff.setLastName(request.getLastName());
        staff.setGender(request.getGender());
        staff.setDateOfBirth(request.getDateOfBirth());
        staff.setPhone(request.getPhone());
        staff.setAddressLine1(request.getAddressLine1());
        staff.setAddressLine2(request.getAddressLine2());
        staff.setCityName(request.getCityName());
        staff.setStateName(request.getStateName());
        staff.setHub(hub);

        Staff updatedStaff = staffRepository.save(staff);

        return new ApiResponse<>(
                true,
                "Staff updated successfully",
                StaffResponse.fromEntity(updatedStaff)
        );
    }

}
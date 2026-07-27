package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.AddonRequest;
import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.request.GuestBookingRequest;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.entity.BookingDetail;
import com.example.demo.entity.BookingHeader;
import com.example.demo.entity.base.Customer;
import com.example.demo.enums.BookingSource;
import com.example.demo.enums.BookingStatus;
import com.example.demo.repository.BookingDetailRepository;
import com.example.demo.repository.BookingHeaderRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.BookingService;
import com.example.demo.service.EmailService;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingHeaderRepository bookingHeaderRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    public BookingServiceImpl(
            BookingHeaderRepository bookingHeaderRepository,
            BookingDetailRepository bookingDetailRepository,
            CustomerRepository customerRepository,
            EmailService emailService)
    {

        this.bookingHeaderRepository = bookingHeaderRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
    }
    
    @Override
    @Transactional
    public ApiResponse<BookingResponse> createBooking(
            BookingRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            throw new RuntimeException(
                    "Customer is not authenticated");
        }

        String email = authentication.getName();

        Customer customer = customerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found"));

        BookingResponse response =
                saveBooking(customer, request);

        // Send booking confirmation email
        emailService.sendBookingConfirmation(response);

        return new ApiResponse<>(
                true,
                "Booking confirmed successfully",
                response);
    }


    private BookingResponse saveBooking(
            Customer customer,
            BookingRequest request) {

        BookingHeader booking = new BookingHeader();

        booking.setDate(LocalDateTime.now());

        // Customer snapshot
        booking.setCustomerId(customer.getCustomerId());
        booking.setFirstName(customer.getFirstName());
        booking.setLastName(customer.getLastName());
        booking.setEmail(customer.getEmail());
        booking.setPhone(customer.getPhone());

        booking.setDrivingLicenseNo(
                customer.getDrivingLicenseNo());

        booking.setPassportNo(
                customer.getPassportNo());

        booking.setAddressLine1(
                customer.getAddressLine1());

        booking.setAddressLine2(
                customer.getAddressLine2());

        booking.setCityId(
                customer.getCity().getCityId());

        booking.setStateId(
                customer.getState().getStateId());

        booking.setPincode(
                customer.getPincode());

        // Booking information
        booking.setCarId(request.getCarId());
        booking.setCarTypeId(request.getCarTypeId());

        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());

        booking.setPickupHubName(
                request.getPickupHubName());

        booking.setDropoffHubName(
                request.getDropoffHubName());

        // Rate snapshot
        booking.setDailyRate(request.getDailyRate());
        booking.setWeeklyRate(request.getWeeklyRate());
        booking.setMonthlyRate(request.getMonthlyRate());

        // Frontend-calculated amounts
        booking.setDuration(request.getDuration());
        booking.setVehicleAmount(request.getVehicleAmount());
        booking.setAddonAmount(request.getAddonAmount());
        booking.setTaxAmount(request.getTaxAmount());
        booking.setGrandTotal(request.getGrandTotal());

        booking.setBookingStatus(
                BookingStatus.PENDING);

        BookingHeader savedBooking =
                bookingHeaderRepository.save(booking);

        // Save add-ons
        if (request.getAddons() != null
                && !request.getAddons().isEmpty()) {

            List<BookingDetail> details = new ArrayList<>();

            for (AddonRequest addon : request.getAddons()) {

                BookingDetail detail = new BookingDetail();

                detail.setBookingId(
                        savedBooking.getBookingId());

                detail.setAddonId(addon.getAddonId());
                detail.setAddonName(addon.getAddonName());
                detail.setAddonPrice(addon.getAddonPrice());

                detail.setBookingSource(
                        BookingSource.ONLINE);

                details.add(detail);
            }

            bookingDetailRepository.saveAll(details);
        }

        // Response
        BookingResponse response = new BookingResponse();

        response.setBookingId(savedBooking.getBookingId());
        response.setCustomerId(savedBooking.getCustomerId());

        String customerName = savedBooking.getFirstName();

        if (savedBooking.getLastName() != null) {
            customerName += " " + savedBooking.getLastName();
        }

        response.setCustomerName(customerName);

        response.setEmail(savedBooking.getEmail());
        response.setCarId(savedBooking.getCarId());

        response.setStartDate(savedBooking.getStartDate());
        response.setEndDate(savedBooking.getEndDate());

        response.setPickupHubName(
                savedBooking.getPickupHubName());

        response.setDropoffHubName(
                savedBooking.getDropoffHubName());

        response.setDuration(savedBooking.getDuration());

        response.setVehicleAmount(
                savedBooking.getVehicleAmount());

        response.setAddonAmount(
                savedBooking.getAddonAmount());

        response.setTaxAmount(
                savedBooking.getTaxAmount());

        response.setGrandTotal(
                savedBooking.getGrandTotal());

        response.setBookingStatus(
                savedBooking.getBookingStatus());

        return response;
    }
   
    private BookingResponse saveGuestBooking(
            GuestBookingRequest request) {

        BookingRequest bookingRequest =
                request.getBooking();

        BookingHeader booking =
                new BookingHeader();

        booking.setDate(LocalDateTime.now());

        // --------------------------------
        // Guest snapshot
        // --------------------------------

        // Guest is NOT a registered customer
        booking.setCustomerId(null);

        booking.setFirstName(
                request.getFirstName());

        booking.setLastName(
                request.getLastName());

        booking.setEmail(
                request.getEmail());

        booking.setPhone(
                request.getPhone());

        booking.setDrivingLicenseNo(
                request.getDrivingLicenseNo());

        booking.setPassportNo(
                request.getPassportNo());

        booking.setAddressLine1(
                request.getAddressLine1());

        booking.setAddressLine2(
                request.getAddressLine2());

        booking.setCityId(
                request.getCityId());

        booking.setStateId(
                request.getStateId());

        booking.setPincode(
                request.getPincode());

        // --------------------------------
        // Booking information
        // --------------------------------

        booking.setCarId(
                bookingRequest.getCarId());

        booking.setCarTypeId(
                bookingRequest.getCarTypeId());

        booking.setStartDate(
                bookingRequest.getStartDate());

        booking.setEndDate(
                bookingRequest.getEndDate());

        booking.setPickupHubName(
                bookingRequest.getPickupHubName());

        booking.setDropoffHubName(
                bookingRequest.getDropoffHubName());

        // --------------------------------
        // Rate snapshot
        // --------------------------------

        booking.setDailyRate(
                bookingRequest.getDailyRate());

        booking.setWeeklyRate(
                bookingRequest.getWeeklyRate());

        booking.setMonthlyRate(
                bookingRequest.getMonthlyRate());

        // --------------------------------
        // Frontend-calculated amounts
        // --------------------------------

        booking.setDuration(
                bookingRequest.getDuration());

        booking.setVehicleAmount(
                bookingRequest.getVehicleAmount());

        booking.setAddonAmount(
                bookingRequest.getAddonAmount());

        booking.setTaxAmount(
                bookingRequest.getTaxAmount());

        booking.setGrandTotal(
                bookingRequest.getGrandTotal());

        booking.setBookingStatus(
                BookingStatus.PENDING);

        // --------------------------------
        // Save header
        // --------------------------------

        BookingHeader savedBooking =
                bookingHeaderRepository.save(booking);

        // --------------------------------
        // Save addons
        // --------------------------------

        if (bookingRequest.getAddons() != null
                && !bookingRequest.getAddons().isEmpty()) {

            List<BookingDetail> details =
                    new ArrayList<>();

            for (AddonRequest addon :
                    bookingRequest.getAddons()) {

                BookingDetail detail =
                        new BookingDetail();

                detail.setBookingId(
                        savedBooking.getBookingId());

                detail.setAddonId(
                        addon.getAddonId());

                detail.setAddonName(
                        addon.getAddonName());

                detail.setAddonPrice(
                        addon.getAddonPrice());

                detail.setBookingSource(
                        BookingSource.ONLINE);

                details.add(detail);
            }

            bookingDetailRepository.saveAll(details);
        }

        // --------------------------------
        // Response
        // --------------------------------

        BookingResponse response =
                new BookingResponse();

        response.setBookingId(
                savedBooking.getBookingId());

        // Will be null for guest
        response.setCustomerId(null);

        String customerName =
                savedBooking.getFirstName();

        if (savedBooking.getLastName() != null) {
            customerName +=
                    " " + savedBooking.getLastName();
        }

        response.setCustomerName(customerName);

        response.setEmail(
                savedBooking.getEmail());

        response.setCarId(
                savedBooking.getCarId());

        response.setStartDate(
                savedBooking.getStartDate());

        response.setEndDate(
                savedBooking.getEndDate());

        response.setPickupHubName(
                savedBooking.getPickupHubName());

        response.setDropoffHubName(
                savedBooking.getDropoffHubName());

        response.setDuration(
                savedBooking.getDuration());

        response.setVehicleAmount(
                savedBooking.getVehicleAmount());

        response.setAddonAmount(
                savedBooking.getAddonAmount());

        response.setTaxAmount(
                savedBooking.getTaxAmount());

        response.setGrandTotal(
                savedBooking.getGrandTotal());

        response.setBookingStatus(
                savedBooking.getBookingStatus());

        return response;
    }
    
    @Override
    @Transactional
    public ApiResponse<BookingResponse> createGuestBooking(
            GuestBookingRequest request) {

        BookingResponse bookingResponse =
                saveGuestBooking(request);

        emailService.sendBookingConfirmation(
                bookingResponse);

        return new ApiResponse<>(
                true,
                "Guest booking confirmed successfully",
                bookingResponse);
    }

}
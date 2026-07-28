package com.example.demo.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.AssignVehicleRequest;
import com.example.demo.dto.response.AssignVehicleResponse;
import com.example.demo.entity.BookingHeader;
import com.example.demo.entity.InvoiceDetail;
import com.example.demo.entity.InvoiceHeader;
import com.example.demo.entity.base.Car;
import com.example.demo.entity.base.Staff;
import com.example.demo.enums.BookingStatus;
import com.example.demo.enums.CarStatus;
import com.example.demo.repository.BookingDetailRepository;
import com.example.demo.repository.BookingHeaderRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.InvoiceDetailRepository;
import com.example.demo.repository.InvoiceHeaderRepository;
import com.example.demo.repository.StaffRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.HandoverService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.dto.request.ConfirmHandoverRequest;
import com.example.demo.dto.request.ReturnVehicleRequest;
import com.example.demo.entity.BookingDetail;

@Service
public class HandoverServiceImpl implements HandoverService {

    // =====================================================
    // Repositories
    // =====================================================

    private final BookingHeaderRepository bookingHeaderRepository;

    private final CarRepository carRepository;

    private final StaffRepository staffRepository;
    
    private final InvoiceHeaderRepository invoiceHeaderRepository;

    private final InvoiceDetailRepository invoiceDetailRepository;

    private final BookingDetailRepository bookingDetailRepository;

    // =====================================================
    // Constructor Injection
    // =====================================================

    
    public HandoverServiceImpl(
            BookingHeaderRepository bookingHeaderRepository,
            CarRepository carRepository,
            StaffRepository staffRepository,
            BookingDetailRepository bookingDetailRepository,
            InvoiceHeaderRepository invoiceHeaderRepository,
            InvoiceDetailRepository invoiceDetailRepository) {

        this.bookingHeaderRepository = bookingHeaderRepository;
        this.carRepository = carRepository;
        this.staffRepository = staffRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.invoiceHeaderRepository = invoiceHeaderRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
    }

    // =====================================================
    // Assign Vehicle
    // =====================================================

    @Override
    @Transactional
    public ApiResponse<AssignVehicleResponse> assignVehicle(
            AssignVehicleRequest request) {

        // -------------------------------------------------
        // Step 1 : Get Logged-in Staff
        // -------------------------------------------------

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(
                        authentication.getPrincipal())) {

            throw new RuntimeException(
                    "Staff is not authenticated");
        }

        String email =
                authentication.getName();

        // -------------------------------------------------
        // Step 2 : Find Staff
        // -------------------------------------------------

        Staff staff =
                staffRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Staff not found"));

        Integer staffHubId =
                staff.getHub().getHubId();

        // -------------------------------------------------
        // Step 3 : Find Booking
        // -------------------------------------------------

        BookingHeader booking =
                bookingHeaderRepository
                        .findById(request.getBookingId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found"));

        // -------------------------------------------------
        // Step 4 : Booking must be Pending
        // -------------------------------------------------

        if (booking.getBookingStatus()
                != BookingStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending bookings can be assigned");
        }

        // -------------------------------------------------
        // Step 5 : Check if vehicle already assigned
        // -------------------------------------------------

        if (booking.getAssignedCarId() != null) {

            throw new RuntimeException(
                    "Vehicle is already assigned to this booking");
        }

        // -------------------------------------------------
        // Step 6 : Find Selected Vehicle
        // -------------------------------------------------

        Car car =
                carRepository
                        .findById(request.getCarId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vehicle not found"));
        
        // -------------------------------------------------
        // Step 7 : Validate Vehicle belongs to Staff Hub
        // -------------------------------------------------

        if (!car.getHub()
                .getHubId()
                .equals(staffHubId)) {

            throw new RuntimeException(
                    "Vehicle does not belong to your hub");
        }

        // -------------------------------------------------
        // Step 8 : Vehicle must be AVAILABLE
        // -------------------------------------------------

        if (car.getStatus() != CarStatus.AVAILABLE) {

            throw new RuntimeException(
                    "Vehicle is not available");
        }

        // -------------------------------------------------
        // Step 9 : Assign Vehicle to Booking
        // -------------------------------------------------

        // Assign Vehicle Id
        
        booking.setAssignedCarId(car.getCarId());

        // Store Vehicle Snapshot
        booking.setAssignedCarRegistrationNo(
                car.getRegistrationNo());

        booking.setAssignedCarBrandName(
                car.getBrandName());

        booking.setAssignedCarModelName(
                car.getModelName());

        // Save Booking
        bookingHeaderRepository.save(booking);

        // -------------------------------------------------
        // Step 10 : Prepare Response
        // -------------------------------------------------

        AssignVehicleResponse response =
                new AssignVehicleResponse();

        response.setBookingId(
                booking.getBookingId());

        response.setAssignedCarId(
                car.getCarId());

        response.setCarRegistrationNo(
                car.getRegistrationNo());

        response.setBrandName(
                car.getBrandName());

        response.setModelName(
                car.getModelName());

        response.setMessage(
                "Vehicle assigned successfully");

        // -------------------------------------------------
        // Step 11 : Return Response
        // -------------------------------------------------

        return new ApiResponse<>(
                true,
                "Vehicle assigned successfully",
                response);
    }
    
    
    @Override
    @Transactional
    public ApiResponse<String> confirmHandover(
            ConfirmHandoverRequest request) {

        // =====================================================
        // Step 1 : Get Logged-in Staff
        // =====================================================

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            throw new RuntimeException("Staff is not authenticated.");
        }

        String email = authentication.getName();

        // =====================================================
        // Step 2 : Find Staff
        // =====================================================

        Staff staff =
                staffRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("Staff not found."));

        Integer staffHubId =
                staff.getHub().getHubId();

        // =====================================================
        // Step 3 : Find Booking
        // =====================================================

        BookingHeader booking =
                bookingHeaderRepository
                        .findById(request.getBookingId())
                        .orElseThrow(() ->
                                new RuntimeException("Booking not found."));

        // =====================================================
        // Step 4 : Booking Status Validation
        // =====================================================

        if (booking.getBookingStatus() != BookingStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending bookings can be handed over.");
        }

        // =====================================================
        // Step 5 : Vehicle Must Be Assigned
        // =====================================================

        if (booking.getAssignedCarId() == null) {

            throw new RuntimeException(
                    "Please assign a vehicle before handover.");
        }

        // =====================================================
        // Step 6 : Find Assigned Vehicle
        // =====================================================

        Car car =
                carRepository
                        .findById(booking.getAssignedCarId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assigned vehicle not found."));

        // =====================================================
        // Step 7 : Hub Validation
        // =====================================================

        if (!car.getHub()
                .getHubId()
                .equals(staffHubId)) {

            throw new RuntimeException(
                    "Vehicle does not belong to your hub.");
        }

        // =====================================================
        // Step 8 : Vehicle Status Validation
        // =====================================================

        if (car.getStatus() != CarStatus.AVAILABLE) {

            throw new RuntimeException(
                    "Vehicle is not available for handover.");
        }
        
        // Check Duplicate Invoice
        // =====================================================

        invoiceHeaderRepository
             .findByBookingId(booking.getBookingId())
             .ifPresent(existing -> {
                 throw new RuntimeException(
                         "Invoice already exists for this booking.");
             });

        // =====================================================
        // Step 9 : Update Booking
        // =====================================================

        booking.setBookingStatus(
                BookingStatus.CONFIRMED);

        booking.setHandoverDate(
                LocalDateTime.now());

        booking.setFuelLevelOut(
                request.getFuelLevelOut());

        bookingHeaderRepository.save(booking);

        // =====================================================
        // Step 10 : Update Vehicle
        // =====================================================

        car.setStatus(CarStatus.RENTED);

        carRepository.save(car);
        
        // =====================================================


        // =====================================================
        // Step 11 : Create Invoice Header
        // =====================================================

        InvoiceHeader invoice = new InvoiceHeader();
        
        // =====================================================
        // Step 12 : Copy Booking Information To Invoice
        // =====================================================

        // Booking Reference
        invoice.setBookingId(booking.getBookingId());

        // Customer Snapshot
        invoice.setCustomerId(booking.getCustomerId());
        invoice.setFirstName(booking.getFirstName());
        invoice.setLastName(booking.getLastName());
        invoice.setEmail(booking.getEmail());
        invoice.setPhone(booking.getPhone());
        invoice.setDrivingLicenseNo(booking.getDrivingLicenseNo());
        invoice.setPassportNo(booking.getPassportNo());

        // Vehicle Snapshot
        invoice.setCarId(car.getCarId());
        invoice.setRegistrationNo(
                booking.getAssignedCarRegistrationNo());

        invoice.setBrandName(
                booking.getAssignedCarBrandName());

        invoice.setModelName(
                booking.getAssignedCarModelName());
        invoice.setCarTypeId(booking.getCarTypeId());

        // Hub Snapshot
        invoice.setPickupHubId(booking.getPickupHubId());
        invoice.setPickupHubName(booking.getPickupHubName());

        invoice.setDropoffHubId(booking.getDropoffHubId());
        invoice.setDropoffHubName(booking.getDropoffHubName());

        // Booking Dates
        invoice.setStartDate(booking.getStartDate());
        invoice.setEndDate(booking.getEndDate());
        invoice.setHandoverDate(booking.getHandoverDate());

        // Charges
        invoice.setDuration(booking.getDuration());

        invoice.setDailyRate(booking.getDailyRate());
        invoice.setWeeklyRate(booking.getWeeklyRate());
        invoice.setMonthlyRate(booking.getMonthlyRate());

        invoice.setVehicleAmount(booking.getVehicleAmount());
        invoice.setAddonAmount(booking.getAddonAmount());
        invoice.setTaxAmount(booking.getTaxAmount());
        invoice.setGrandTotal(booking.getGrandTotal());

        // Fuel
        invoice.setFuelLevelOut(booking.getFuelLevelOut());

        // =====================================================
        // Step 13 : Save Invoice Header
        // =====================================================

        invoice = invoiceHeaderRepository.save(invoice);

        // =====================================================
        // Step 14 : Copy Booking Details To Invoice Details
        // =====================================================

        List<BookingDetail> bookingDetails =
                bookingDetailRepository.findByBookingId(
                        booking.getBookingId());

        for (BookingDetail detail : bookingDetails) {

            InvoiceDetail invoiceDetail = new InvoiceDetail();

            invoiceDetail.setInvoiceId(
                    invoice.getInvoiceId());

            invoiceDetail.setBookingDetailId(
                    detail.getBookingDetailId());

            invoiceDetail.setAddonId(
                    detail.getAddonId());

            invoiceDetail.setAddonName(
                    detail.getAddonName());

            invoiceDetail.setAddonPrice(
                    detail.getAddonPrice());
            
            invoiceDetailRepository.save(invoiceDetail);
        }
        
        // =====================================================
        // Step 15 : Return Success Response
        // =====================================================

        return new ApiResponse<>(
                true,
                "Vehicle handover completed successfully.",
                "Invoice generated successfully.");
    }
    
    
    @Override
    @Transactional
    public ApiResponse<String> returnVehicle(
            ReturnVehicleRequest request) {

        // =====================================================
        // Step 1 : Get Logged-in Staff
        // =====================================================

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            throw new RuntimeException(
                    "Staff is not authenticated.");
        }

        String email = authentication.getName();
        

        // =====================================================
        // Step 2 : Find Staff
        // =====================================================

        Staff staff =
                staffRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Staff not found."));

        Integer staffHubId =
                staff.getHub().getHubId();

        // =====================================================
        // Step 3 : Find Booking
        // =====================================================

        BookingHeader booking =
                bookingHeaderRepository
                        .findById(request.getBookingId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found."));

        // =====================================================
        // Step 4 : Booking Status Validation
        // =====================================================

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {

            throw new RuntimeException(
                    "Only confirmed bookings can be returned.");
        }

        // =====================================================
        // Step 5 : Vehicle Must Be Assigned
        // =====================================================

        if (booking.getAssignedCarId() == null) {

            throw new RuntimeException(
                    "No vehicle is assigned to this booking.");
        }

        // =====================================================
        // Step 6 : Find Assigned Vehicle
        // =====================================================

        Car car =
                carRepository
                        .findById(booking.getAssignedCarId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Assigned vehicle not found."));

        // =====================================================
        // Step 7 : Hub Validation
        // =====================================================

        if (!car.getHub()
                .getHubId()
                .equals(staffHubId)) {

            throw new RuntimeException(
                    "Vehicle does not belong to your hub.");
        }

        // =====================================================
        // Step 8 : Vehicle Status Validation
        // =====================================================

        if (car.getStatus() != CarStatus.RENTED) {

            throw new RuntimeException(
                    "Vehicle is not currently rented.");
        }

        // =====================================================
        // Step 9 : Find Invoice
        // =====================================================

        InvoiceHeader invoice =
                invoiceHeaderRepository
                        .findByBookingId(booking.getBookingId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invoice not found."));

        // =====================================================
        // Step 10 : Update Booking
        // =====================================================

        // Override planned return date with actual return date
        booking.setEndDate(LocalDate.now());

        booking.setFuelLevelIn(
                request.getFuelLevelIn());

        booking.setBookingStatus(
                BookingStatus.COMPLETED);

        bookingHeaderRepository.save(booking);

        // =====================================================
        // Step 11 : Update Invoice
        // =====================================================

        invoice.setEndDate(
                booking.getEndDate());

        invoice.setFuelLevelIn(
                request.getFuelLevelIn());

        invoice.setFuelCharges(
                request.getFuelCharges());

        invoiceHeaderRepository.save(invoice);

        // =====================================================
        // Step 12 : Update Vehicle
        // =====================================================

        car.setStatus(CarStatus.AVAILABLE);

        carRepository.save(car);

        // =====================================================
        // Step 13 : Return Success Response
        // =====================================================

        return new ApiResponse<>(
                true,
                "Vehicle returned successfully.",
                "Booking completed successfully.");
    }

}
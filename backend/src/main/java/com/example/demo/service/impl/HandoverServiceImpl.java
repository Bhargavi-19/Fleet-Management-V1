package com.example.demo.service.impl;

<<<<<<< HEAD
=======
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
>>>>>>> Developer
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< HEAD
import com.example.demo.dto.request.AssignVehicleRequest;
import com.example.demo.dto.response.AssignVehicleResponse;
=======
import com.example.demo.exception.error.BusinessException;
import com.example.demo.exception.error.ResourceNotFoundException;
import com.example.demo.exception.error.UnauthorizedActionException;
import com.example.demo.dto.request.AssignVehicleRequest;
import com.example.demo.dto.response.AssignVehicleResponse;
import com.example.demo.dto.response.InvoiceResponse;
>>>>>>> Developer
import com.example.demo.entity.BookingHeader;
import com.example.demo.entity.InvoiceDetail;
import com.example.demo.entity.InvoiceHeader;
import com.example.demo.entity.base.Car;
<<<<<<< HEAD
=======
import com.example.demo.entity.base.Hub;
>>>>>>> Developer
import com.example.demo.entity.base.Staff;
import com.example.demo.enums.BookingStatus;
import com.example.demo.enums.CarStatus;
import com.example.demo.repository.BookingDetailRepository;
import com.example.demo.repository.BookingHeaderRepository;
import com.example.demo.repository.CarRepository;
<<<<<<< HEAD
=======
import com.example.demo.repository.HubRepository;
>>>>>>> Developer
import com.example.demo.repository.InvoiceDetailRepository;
import com.example.demo.repository.InvoiceHeaderRepository;
import com.example.demo.repository.StaffRepository;
import com.example.demo.response.ApiResponse;
<<<<<<< HEAD
import com.example.demo.service.HandoverService;
=======
import com.example.demo.service.EmailService;
import com.example.demo.service.HandoverService;
import com.example.demo.service.InvoiceService;
>>>>>>> Developer

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
<<<<<<< HEAD
=======

    private final HubRepository hubRepository;
>>>>>>> Developer
    
    private final InvoiceHeaderRepository invoiceHeaderRepository;

    private final InvoiceDetailRepository invoiceDetailRepository;

    private final BookingDetailRepository bookingDetailRepository;

<<<<<<< HEAD
=======
    private final InvoiceService invoiceService;

    private final EmailService emailService;

    private static final Logger log =
            LoggerFactory.getLogger(HandoverServiceImpl.class);

>>>>>>> Developer
    // =====================================================
    // Constructor Injection
    // =====================================================

    
    public HandoverServiceImpl(
            BookingHeaderRepository bookingHeaderRepository,
            CarRepository carRepository,
            StaffRepository staffRepository,
            BookingDetailRepository bookingDetailRepository,
            InvoiceHeaderRepository invoiceHeaderRepository,
<<<<<<< HEAD
            InvoiceDetailRepository invoiceDetailRepository) {
=======
            InvoiceDetailRepository invoiceDetailRepository,
            HubRepository hubRepository,
            InvoiceService invoiceService,
            EmailService emailService) {
>>>>>>> Developer

        this.bookingHeaderRepository = bookingHeaderRepository;
        this.carRepository = carRepository;
        this.staffRepository = staffRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.invoiceHeaderRepository = invoiceHeaderRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
<<<<<<< HEAD
=======
        this.hubRepository = hubRepository;
        this.invoiceService = invoiceService;
        this.emailService = emailService;
>>>>>>> Developer
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

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new UnauthorizedActionException(
>>>>>>> Developer
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
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
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
<<<<<<< HEAD
                                new RuntimeException(
                                        "Booking not found"));

        // -------------------------------------------------
=======
                                new ResourceNotFoundException(
                                        "Booking not found"));

        // -------------------------------------------------
        // Step 3b : This hub must be the PICK-UP hub
        //
        // A one-way rental is handed over by the pick-up hub only. The
        // drop-off hub sees it later, in the Return module.
        // -------------------------------------------------

        if (booking.getPickupHubId() == null
                || !booking.getPickupHubId().equals(staffHubId)) {

            throw new UnauthorizedActionException(
                    "This booking is collected from a different hub, so it "
                    + "cannot be handed over here.");
        }

        // -------------------------------------------------
>>>>>>> Developer
        // Step 4 : Booking must be Pending
        // -------------------------------------------------

        if (booking.getBookingStatus()
                != BookingStatus.PENDING) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Only pending bookings can be assigned");
        }

        // -------------------------------------------------
        // Step 5 : Check if vehicle already assigned
        // -------------------------------------------------

        if (booking.getAssignedCarId() != null) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Vehicle is already assigned to this booking");
        }

        // -------------------------------------------------
        // Step 6 : Find Selected Vehicle
        // -------------------------------------------------

        Car car =
                carRepository
                        .findById(request.getCarId())
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
                                        "Vehicle not found"));
        
        // -------------------------------------------------
        // Step 7 : Validate Vehicle belongs to Staff Hub
        // -------------------------------------------------

        if (!car.getHub()
                .getHubId()
                .equals(staffHubId)) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Vehicle does not belong to your hub");
        }

        // -------------------------------------------------
        // Step 8 : Vehicle must be AVAILABLE
        // -------------------------------------------------

        if (car.getStatus() != CarStatus.AVAILABLE) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Vehicle is not available");
        }

        // -------------------------------------------------
<<<<<<< HEAD
=======
        // Step 8b : Vehicle must match the booked category
        //
        // The customer paid for a specific car type, so staff cannot hand
        // over a different one. The screen already filters the list, but
        // this is the check that actually enforces it.
        // -------------------------------------------------

        if (booking.getCarTypeId() != null) {

            if (car.getCarType() == null
                    || !booking.getCarTypeId()
                            .equals(car.getCarType().getCarTypeId().longValue())) {

                throw new BusinessException(
                        "This vehicle is a different category from the one booked. "
                        + "Please choose a vehicle of the booked car type.");
            }
        }

        // -------------------------------------------------
>>>>>>> Developer
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

<<<<<<< HEAD
            throw new RuntimeException("Staff is not authenticated.");
=======
            throw new UnauthorizedActionException("Staff is not authenticated.");
>>>>>>> Developer
        }

        String email = authentication.getName();

        // =====================================================
        // Step 2 : Find Staff
        // =====================================================

        Staff staff =
                staffRepository.findByEmail(email)
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException("Staff not found."));
=======
                                new ResourceNotFoundException("Staff not found."));
>>>>>>> Developer

        Integer staffHubId =
                staff.getHub().getHubId();

        // =====================================================
        // Step 3 : Find Booking
        // =====================================================

        BookingHeader booking =
                bookingHeaderRepository
                        .findById(request.getBookingId())
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException("Booking not found."));
=======
                                new ResourceNotFoundException("Booking not found."));

        // =====================================================
        // Step 3b : This hub must be the PICK-UP hub
        // =====================================================

        if (booking.getPickupHubId() == null
                || !booking.getPickupHubId().equals(staffHubId)) {

            throw new UnauthorizedActionException(
                    "This booking is collected from a different hub, so it "
                    + "cannot be handed over here.");
        }
>>>>>>> Developer

        // =====================================================
        // Step 4 : Booking Status Validation
        // =====================================================

        if (booking.getBookingStatus() != BookingStatus.PENDING) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Only pending bookings can be handed over.");
        }

        // =====================================================
        // Step 5 : Vehicle Must Be Assigned
        // =====================================================

        if (booking.getAssignedCarId() == null) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Please assign a vehicle before handover.");
        }

        // =====================================================
        // Step 6 : Find Assigned Vehicle
        // =====================================================

        Car car =
                carRepository
                        .findById(booking.getAssignedCarId())
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
                                        "Assigned vehicle not found."));

        // =====================================================
        // Step 7 : Hub Validation
        // =====================================================

        if (!car.getHub()
                .getHubId()
                .equals(staffHubId)) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Vehicle does not belong to your hub.");
        }

        // =====================================================
        // Step 8 : Vehicle Status Validation
        // =====================================================

        if (car.getStatus() != CarStatus.AVAILABLE) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Vehicle is not available for handover.");
        }
        
        // Check Duplicate Invoice
        // =====================================================

        invoiceHeaderRepository
             .findByBookingId(booking.getBookingId())
             .ifPresent(existing -> {
<<<<<<< HEAD
                 throw new RuntimeException(
=======
                 throw new BusinessException(
>>>>>>> Developer
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

<<<<<<< HEAD
=======
        // Address snapshot (BRD: Invoice Header Table)
        invoice.setAddressLine1(booking.getAddressLine1());
        invoice.setAddressLine2(booking.getAddressLine2());
        invoice.setCityId(booking.getCityId());
        invoice.setStateId(booking.getStateId());
        invoice.setPincode(booking.getPincode());

>>>>>>> Developer
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

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new UnauthorizedActionException(
>>>>>>> Developer
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
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
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
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
                                        "Booking not found."));

        // =====================================================
        // Step 4 : Booking Status Validation
        // =====================================================

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Only confirmed bookings can be returned.");
        }

        // =====================================================
        // Step 5 : Vehicle Must Be Assigned
        // =====================================================

        if (booking.getAssignedCarId() == null) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "No vehicle is assigned to this booking.");
        }

        // =====================================================
        // Step 6 : Find Assigned Vehicle
        // =====================================================

        Car car =
                carRepository
                        .findById(booking.getAssignedCarId())
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
                                        "Assigned vehicle not found."));

        // =====================================================
        // Step 7 : Hub Validation
<<<<<<< HEAD
        // =====================================================

        if (!car.getHub()
                .getHubId()
                .equals(staffHubId)) {

            throw new RuntimeException(
                    "Vehicle does not belong to your hub.");
=======
        //
        // Checked against the booking's DROP-OFF hub, not the car's hub.
        // On a one-way rental (BOM -> Nagpur) the car still belongs to BOM
        // at this moment, so testing the car's hub would make it impossible
        // to ever accept the return at Nagpur.
        // =====================================================

        if (booking.getDropoffHubId() == null
                || !booking.getDropoffHubId().equals(staffHubId)) {

            throw new UnauthorizedActionException(
                    "This booking is due back at a different hub, so it "
                    + "cannot be returned here.");
>>>>>>> Developer
        }

        // =====================================================
        // Step 8 : Vehicle Status Validation
        // =====================================================

        if (car.getStatus() != CarStatus.RENTED) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Vehicle is not currently rented.");
        }

        // =====================================================
        // Step 9 : Find Invoice
        // =====================================================

        InvoiceHeader invoice =
                invoiceHeaderRepository
                        .findByBookingId(booking.getBookingId())
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
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
<<<<<<< HEAD
        // Step 11 : Update Invoice
        // =====================================================

=======
        // Step 11 : Finalise the Invoice
        //
        // The invoice row was created at hand-over. The vehicle is only now
        // back, so this is the point at which the invoice is actually
        // "raised": it gets its number, its date and the return details.
        // =====================================================

        LocalDateTime now = LocalDateTime.now();

>>>>>>> Developer
        invoice.setEndDate(
                booking.getEndDate());

        invoice.setFuelLevelIn(
                request.getFuelLevelIn());

        invoice.setFuelCharges(
                request.getFuelCharges());

<<<<<<< HEAD
=======
        invoice.setReturnDate(now);
        invoice.setInvoiceDate(now);

        // Only number it once, so a corrected return never renumbers it.
        if (invoice.getInvoiceNo() == null) {
            invoice.setInvoiceNo(buildInvoiceNumber(invoice.getInvoiceId(), now));
        }

>>>>>>> Developer
        invoiceHeaderRepository.save(invoice);

        // =====================================================
        // Step 12 : Update Vehicle
<<<<<<< HEAD
=======
        //
        // The car is now standing at the drop-off hub, so move it there.
        // Without this a one-way rental would leave the vehicle listed at
        // its original hub and it could never be hired out again.
>>>>>>> Developer
        // =====================================================

        car.setStatus(CarStatus.AVAILABLE);

<<<<<<< HEAD
        carRepository.save(car);

        // =====================================================
        // Step 13 : Return Success Response
=======
        if (!car.getHub().getHubId().equals(staffHubId)) {

            Hub dropoffHub = hubRepository
                    .findById(staffHubId)
                    .orElseThrow(() -> new ResourceNotFoundException("Hub not found"));

            car.setHub(dropoffHub);

            log.info("Vehicle {} moved from hub {} to hub {} on return of booking {}",
                    car.getRegistrationNo(), booking.getPickupHubId(),
                    staffHubId, booking.getBookingId());
        }

        carRepository.save(car);

        // =====================================================
        // Step 13 : Email the invoice
        //
        // Sent on a background thread and it never throws, so a mail failure
        // is logged but cannot break the return the staff member just made.
        // =====================================================

        try {
            InvoiceResponse invoiceResponse =
                    invoiceService.loadInvoiceForBooking(booking.getBookingId());

            emailService.sendInvoice(invoiceResponse);

        } catch (Exception e) {
            log.error("Could not queue the invoice email for booking {}: {}",
                    booking.getBookingId(), e.getMessage());
        }

        // =====================================================
        // Step 14 : Return Success Response
>>>>>>> Developer
        // =====================================================

        return new ApiResponse<>(
                true,
                "Vehicle returned successfully.",
<<<<<<< HEAD
                "Booking completed successfully.");
=======
                "Booking completed and invoice generated.");
    }

    /**
     * Builds the printed invoice number, e.g. INV-2026-000042.
     * The invoice id keeps it unique; the year just makes it readable.
     */
    private String buildInvoiceNumber(Long invoiceId, LocalDateTime when) {
        return String.format("INV-%d-%06d", when.getYear(), invoiceId);
>>>>>>> Developer
    }

}
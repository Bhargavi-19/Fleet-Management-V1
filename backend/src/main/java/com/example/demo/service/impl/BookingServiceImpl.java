package com.example.demo.service.impl;

<<<<<<< HEAD
import java.time.LocalDate;
import java.time.LocalDateTime;
=======
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
>>>>>>> Developer
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< HEAD
=======
import com.example.demo.exception.error.BusinessException;
import com.example.demo.exception.error.ResourceNotFoundException;
import com.example.demo.exception.error.UnauthorizedActionException;
>>>>>>> Developer
import com.example.demo.dto.request.AddonRequest;
import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.request.GuestBookingRequest;
import com.example.demo.dto.request.UpdateBookingRequest;
import com.example.demo.dto.request.UpdateBookingStatusRequest;
<<<<<<< HEAD
=======
import com.example.demo.dto.response.BookingAddonResponse;
>>>>>>> Developer
import com.example.demo.dto.response.BookingPageResponse;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.dto.response.BookingStatsResponse;
import com.example.demo.entity.BookingDetail;
import com.example.demo.entity.BookingHeader;
<<<<<<< HEAD
import com.example.demo.entity.base.Customer;
import com.example.demo.entity.base.Staff;
import com.example.demo.enums.BookingSource;
import com.example.demo.enums.BookingStatus;
import com.example.demo.repository.BookingDetailRepository;
import com.example.demo.repository.BookingHeaderRepository;
=======
import com.example.demo.entity.base.Addon;
import com.example.demo.entity.base.CarType;
import com.example.demo.entity.base.City;
import com.example.demo.entity.base.Customer;
import com.example.demo.entity.base.Staff;
import com.example.demo.entity.base.State;
import com.example.demo.enums.BookingSource;
import com.example.demo.enums.BookingStatus;
import com.example.demo.enums.HubScope;
import com.example.demo.repository.BookingDetailRepository;
import com.example.demo.repository.BookingHeaderRepository;
import com.example.demo.repository.AddonRepository;
import com.example.demo.repository.CarTypeRepository;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.StateRepository;
>>>>>>> Developer
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.StaffRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.BookingService;
import com.example.demo.service.EmailService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Service
public class BookingServiceImpl implements BookingService {

    private final BookingHeaderRepository bookingHeaderRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private final StaffRepository staffRepository;
<<<<<<< HEAD
=======
    private final CarTypeRepository carTypeRepository;
    private final AddonRepository addonRepository;
    private final CityRepository cityRepository;
    private final StateRepository stateRepository;
>>>>>>> Developer

    public BookingServiceImpl(
            BookingHeaderRepository bookingHeaderRepository,
            BookingDetailRepository bookingDetailRepository,
            CustomerRepository customerRepository,
            StaffRepository staffRepository,
<<<<<<< HEAD
=======
            CarTypeRepository carTypeRepository,
            AddonRepository addonRepository,
            CityRepository cityRepository,
            StateRepository stateRepository,
>>>>>>> Developer
            EmailService emailService) {

        this.bookingHeaderRepository = bookingHeaderRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
<<<<<<< HEAD
=======
        this.carTypeRepository = carTypeRepository;
        this.addonRepository = addonRepository;
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
>>>>>>> Developer
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

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new UnauthorizedActionException(
>>>>>>> Developer
                    "Customer is not authenticated");
        }

        String email = authentication.getName();

        Customer customer = customerRepository
                .findByEmail(email)
                .orElseThrow(() ->
<<<<<<< HEAD
                        new RuntimeException(
=======
                        new ResourceNotFoundException(
>>>>>>> Developer
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


<<<<<<< HEAD
=======
    /**
     * Dates a booking must satisfy, whether it is being created or modified.
     *
     * The DTO carries @FutureOrPresent, but this runs too so the rule still
     * holds for the guest path and any future caller.
     */
    private void validateBookingDates(LocalDate startDate, LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new BusinessException("Both the pick-up and return dates are required");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new BusinessException("The pick-up date cannot be in the past");
        }
        if (!endDate.isAfter(startDate)) {
            throw new BusinessException("The return date must be after the pick-up date");
        }
    }

    /** GST applied to the vehicle and add-on subtotal. */
    private static final BigDecimal TAX_RATE = new BigDecimal("0.18");

    /** Null-safe Double -> BigDecimal, so a missing rate counts as zero. */
    private BigDecimal toAmount(Double value) {
        return value == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Prices the rental using the best combination of monthly, weekly and
     * daily rates - the same tiering the booking screen shows the customer.
     *
     * 40 days = 1 month + 1 week + 3 days.
     */
    private BigDecimal calculateVehicleAmount(
            BigDecimal daily,
            BigDecimal weekly,
            BigDecimal monthly,
            int days) {

        int months = days / 30;
        int remainder = days % 30;
        int weeks = remainder / 7;
        int extraDays = remainder % 7;

        return monthly.multiply(BigDecimal.valueOf(months))
                .add(weekly.multiply(BigDecimal.valueOf(weeks)))
                .add(daily.multiply(BigDecimal.valueOf(extraDays)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Totals the chosen add-ons using the price held in the database, never
     * the price sent by the browser.
     */
    private BigDecimal calculateAddonAmount(List<AddonRequest> addons, int days) {

        if (addons == null || addons.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (AddonRequest requested : addons) {

            if (requested.getAddonId() == null) {
                continue;
            }

            Addon addon = addonRepository
                    .findById(requested.getAddonId().intValue())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Add-on no longer available: " + requested.getAddonName()));

            total = total.add(
                    toAmount(addon.getPricePerDay())
                            .multiply(BigDecimal.valueOf(days)));
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Copies the details given at booking time onto the customer's profile:
     * licence number, passport number and the billing address.
     *
     * Only fills a value in when the customer supplied one, so an empty field
     * on the booking form never wipes something already on the profile.
     */
    private void saveDocumentsOnProfile(
            Customer customer,
            BookingRequest request) {

        boolean changed = false;

        String licence = request.getDrivingLicenseNo();
        if (licence != null && !licence.isBlank()
                && !licence.equals(customer.getDrivingLicenseNo())) {

            customer.setDrivingLicenseNo(licence.trim());
            changed = true;
        }

        String passport = request.getPassportNo();
        if (passport != null && !passport.isBlank()
                && !passport.equals(customer.getPassportNo())) {

            customer.setPassportNo(passport.trim());
            changed = true;
        }

        // ---- Billing address -------------------------------------------
        // Same idea as the documents above: whatever the customer typed on
        // the booking form becomes their saved address, so the next booking
        // pre-fills it and the invoice always has something to print.
        //
        // Blank values are ignored rather than written. If they were written
        // we would wipe a good address every time the form sent an empty
        // field, which is the opposite of what the customer expects.

        String line1 = request.getAddressLine1();
        if (line1 != null && !line1.isBlank()
                && !line1.trim().equals(customer.getAddressLine1())) {

            customer.setAddressLine1(line1.trim());
            changed = true;
        }

        String line2 = request.getAddressLine2();
        if (line2 != null && !line2.isBlank()
                && !line2.trim().equals(customer.getAddressLine2())) {

            customer.setAddressLine2(line2.trim());
            changed = true;
        }

        String pincode = request.getPincode();
        if (pincode != null && !pincode.isBlank()
                && !pincode.trim().equals(customer.getPincode())) {

            customer.setPincode(pincode.trim());
            changed = true;
        }

        // State and city are real rows, so look them up rather than trusting
        // the id blindly. An id that does not exist is simply skipped: a bad
        // address must never stop somebody renting a car.
        Integer stateId = request.getStateId();
        if (stateId != null
                && (customer.getState() == null
                    || !stateId.equals(customer.getState().getStateId()))) {

            State state = stateRepository.findById(stateId).orElse(null);
            if (state != null) {
                customer.setState(state);
                changed = true;
            }
        }

        Integer cityId = request.getCityId();
        if (cityId != null
                && (customer.getCity() == null
                    || !cityId.equals(customer.getCity().getCityId()))) {

            City city = cityRepository.findById(cityId).orElse(null);
            if (city != null) {
                customer.setCity(city);
                changed = true;
            }
        }

        if (changed) {
            customerRepository.save(customer);
        }
    }

>>>>>>> Developer
    private BookingResponse saveBooking(
            Customer customer,
            BookingRequest request) {

<<<<<<< HEAD
=======
        validateBookingDates(request.getStartDate(), request.getEndDate());

        // If the customer typed a licence number, passport number or address
        // on the booking form, remember it on their profile so they never
        // retype it. This reuses the same Customer record the Update Profile
        // API writes to, and it has to happen BEFORE the snapshot below is
        // taken - the snapshot reads straight off the Customer.
        saveDocumentsOnProfile(customer, request);

>>>>>>> Developer
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

<<<<<<< HEAD
        booking.setCityId(
                customer.getCity().getCityId());

        booking.setStateId(
                customer.getState().getStateId());
=======
        booking.setDateOfBirth(
                customer.getDateOfBirth());

        booking.setGender(
                customer.getGender());

        booking.setNationality(
                customer.getNationality());

        // City / State are optional on the customer record, so only
        // copy them across when they are actually filled in.
        if (customer.getCity() != null) {
            booking.setCityId(
                    customer.getCity().getCityId());
        }

        if (customer.getState() != null) {
            booking.setStateId(
                    customer.getState().getStateId());
        }
>>>>>>> Developer

        booking.setPincode(
                customer.getPincode());

        // Booking information
<<<<<<< HEAD
=======
        // carId stays empty - staff pick the actual vehicle at hand-over.
>>>>>>> Developer
        booking.setCarId(request.getCarId());
        booking.setCarTypeId(request.getCarTypeId());

        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());

        booking.setPickupHubId(
                request.getPickupHubId());

        booking.setPickupHubName(
                request.getPickupHubName());

        booking.setDropoffHubId(
                request.getDropoffHubId());

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

<<<<<<< HEAD
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
   
    
=======
        return mapToBookingResponse(savedBooking);
    }


>>>>>>> Developer
    private BookingResponse saveGuestBooking(
            GuestBookingRequest request) {

        BookingRequest bookingRequest =
                request.getBooking();

<<<<<<< HEAD
=======
        validateBookingDates(
                bookingRequest.getStartDate(),
                bookingRequest.getEndDate());

>>>>>>> Developer
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

        booking.setPickupHubId(
                bookingRequest.getPickupHubId());

        booking.setPickupHubName(
                bookingRequest.getPickupHubName());

        booking.setDropoffHubId(
                bookingRequest.getDropoffHubId());

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

<<<<<<< HEAD
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
=======
        return mapToBookingResponse(savedBooking);
>>>>>>> Developer
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

    
<<<<<<< HEAD
=======
    /**
     * Single place that converts a booking entity into the API response.
     *
     * Used by every read and write method so the customer screens and the
     * staff screens always receive exactly the same shape.
     */
>>>>>>> Developer
    private BookingResponse mapToBookingResponse(
            BookingHeader booking) {

        BookingResponse response =
                new BookingResponse();

<<<<<<< HEAD
        response.setBookingId(
                booking.getBookingId());

=======
        // ---------- Booking ----------
        response.setBookingId(
                booking.getBookingId());

        response.setBookingStatus(
                booking.getBookingStatus());

        response.setBookingDate(
                booking.getDate());

        // ---------- Customer ----------
>>>>>>> Developer
        response.setCustomerId(
                booking.getCustomerId());

        String customerName =
                booking.getFirstName();

        if (booking.getLastName() != null
                && !booking.getLastName().isBlank()) {

            customerName +=
                    " " + booking.getLastName();
        }

        response.setCustomerName(customerName);

        response.setEmail(
                booking.getEmail());

<<<<<<< HEAD
        response.setCarId(
                booking.getCarId());

=======
        response.setPhone(
                booking.getPhone());

        response.setDrivingLicenseNo(
                booking.getDrivingLicenseNo());

        // Structured address, with the city and state resolved to names so
        // every screen can render it the same way.
        response.setAddressLine1(booking.getAddressLine1());
        response.setAddressLine2(booking.getAddressLine2());
        response.setPincode(booking.getPincode());
        response.setCityId(booking.getCityId());
        response.setStateId(booking.getStateId());

        if (booking.getCityId() != null) {
            cityRepository.findById(booking.getCityId())
                    .ifPresent(c -> response.setCityName(c.getCityName()));
        }
        if (booking.getStateId() != null) {
            stateRepository.findById(booking.getStateId())
                    .ifPresent(st -> response.setStateName(st.getStateName()));
        }

        // ---------- Vehicle ----------
        response.setCarId(
                booking.getCarId());

        response.setCarTypeId(
                booking.getCarTypeId());

        // Show the customer the category they actually booked, rather than a
        // placeholder, on the dashboard's Vehicle Information panel.
        if (booking.getCarTypeId() != null) {
            carTypeRepository
                    .findById(booking.getCarTypeId().intValue())
                    .ifPresent(carType -> {
                        response.setCarTypeName(carType.getCarType());
                        response.setCarTypeImageUrl(carType.getImageUrl());
                        if (carType.getCarClass() != null) {
                            response.setCarClass(carType.getCarClass().name());
                        }
                    });
        }

        // ---------- Hubs ----------
>>>>>>> Developer
        response.setPickupHubId(
                booking.getPickupHubId());

        response.setPickupHubName(
                booking.getPickupHubName());

        response.setDropoffHubId(
                booking.getDropoffHubId());

        response.setDropoffHubName(
                booking.getDropoffHubName());

<<<<<<< HEAD
=======
        // ---------- Dates ----------
>>>>>>> Developer
        response.setStartDate(
                booking.getStartDate());

        response.setEndDate(
                booking.getEndDate());

        response.setDuration(
                booking.getDuration());

<<<<<<< HEAD
=======
        // ---------- Rates ----------
        response.setDailyRate(
                booking.getDailyRate());

        response.setWeeklyRate(
                booking.getWeeklyRate());

        response.setMonthlyRate(
                booking.getMonthlyRate());

        // ---------- Amounts ----------
>>>>>>> Developer
        response.setVehicleAmount(
                booking.getVehicleAmount());

        response.setAddonAmount(
                booking.getAddonAmount());

        response.setTaxAmount(
                booking.getTaxAmount());

        response.setGrandTotal(
                booking.getGrandTotal());

<<<<<<< HEAD
        response.setBookingStatus(
                booking.getBookingStatus());
=======
        // ---------- Assigned vehicle ----------
        response.setAssignedCarId(
                booking.getAssignedCarId());

        response.setAssignedCarRegistrationNo(
                booking.getAssignedCarRegistrationNo());

        response.setAssignedCarBrandName(
                booking.getAssignedCarBrandName());

        response.setAssignedCarModelName(
                booking.getAssignedCarModelName());

        // ---------- Hand-over / return ----------
        response.setHandoverDate(
                booking.getHandoverDate());

        response.setFuelLevelOut(
                booking.getFuelLevelOut());

        response.setFuelLevelIn(
                booking.getFuelLevelIn());

        response.setFuelCharges(
                booking.getFuelCharges());

        // ---------- Add-ons ----------
        response.setAddons(
                bookingDetailRepository
                        .findByBookingId(booking.getBookingId())
                        .stream()
                        .map(BookingAddonResponse::fromEntity)
                        .toList());
>>>>>>> Developer

        return response;
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<BookingPageResponse> getBookings(
            int page,
            int size,
            BookingStatus status) {

        // -----------------------------
        // Validate pagination
        // -----------------------------

        if (page < 0) {
<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Page number cannot be negative");
        }

        if (size <= 0) {
<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Page size must be greater than 0");
        }

        // Prevent very large requests
        if (size > 100) {
            size = 100;
        }

        // -----------------------------
        // Get logged-in user
        // -----------------------------

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
                    "User is not authenticated");
        }

        String email =
                authentication.getName();

        // -----------------------------
        // Find logged-in customer
        // -----------------------------

        Customer customer =
                customerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
                                        "Customer not found"));

        // -----------------------------
        // Pagination
        // -----------------------------

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt")
                                .descending());

        Page<BookingHeader> bookingPage;

        // -----------------------------
        // Optional status filter
        // -----------------------------

        if (status != null) {

            bookingPage =
                    bookingHeaderRepository
                            .findByCustomerIdAndBookingStatus(
                                    customer.getCustomerId(),
                                    status,
                                    pageable);

        } else {

            bookingPage =
                    bookingHeaderRepository
                            .findByCustomerId(
                                    customer.getCustomerId(),
                                    pageable);
        }

        // -----------------------------
        // Entity -> DTO
        // -----------------------------

        List<BookingResponse> bookings =
                bookingPage
                        .getContent()
                        .stream()
                        .map(this::mapToBookingResponse)
                        .toList();

        // -----------------------------
        // Pagination response
        // -----------------------------

        BookingPageResponse pageResponse =
                new BookingPageResponse();

        pageResponse.setBookings(bookings);

        pageResponse.setCurrentPage(
                bookingPage.getNumber());

        pageResponse.setPageSize(
                bookingPage.getSize());

        pageResponse.setTotalElements(
                bookingPage.getTotalElements());

        pageResponse.setTotalPages(
                bookingPage.getTotalPages());

        pageResponse.setFirst(
                bookingPage.isFirst());

        pageResponse.setLast(
                bookingPage.isLast());

        return new ApiResponse<>(
                true,
                "Bookings fetched successfully",
                pageResponse);
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<BookingResponse> getBookingById(
            Long bookingId) {

        // Get logged-in user
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
                    "User is not authenticated");
        }

        String email = authentication.getName();

        // Find logged-in customer
        Customer customer =
                customerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
                                        "Customer not found"));

        // Find booking
        BookingHeader booking =
                bookingHeaderRepository
                        .findById(bookingId)
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
                                        "Booking not found"));

        // Ownership check
        if (booking.getCustomerId() == null
                || !booking.getCustomerId()
                        .equals(customer.getCustomerId())) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new UnauthorizedActionException(
>>>>>>> Developer
                    "You are not authorized to access this booking");
        }

        BookingResponse response =
                mapToBookingResponse(booking);

        return new ApiResponse<>(
                true,
                "Booking fetched successfully",
                response);
    }
    
    
    @Override
    @Transactional
    public ApiResponse<BookingResponse> updateBooking(
            Long bookingId,
            UpdateBookingRequest request) {

        // --------------------------------
        // Get logged-in customer
        // --------------------------------

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
                    "User is not authenticated");
        }

        String email = authentication.getName();

        Customer customer =
                customerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
                                        "Customer not found"));

        // --------------------------------
        // Find booking
        // --------------------------------

        BookingHeader booking =
                bookingHeaderRepository
                        .findById(bookingId)
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
                                        "Booking not found"));

        // --------------------------------
        // Ownership check
        // --------------------------------

        if (booking.getCustomerId() == null
                || !booking.getCustomerId()
                        .equals(customer.getCustomerId())) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new UnauthorizedActionException(
>>>>>>> Developer
                    "You are not authorized to modify this booking");
        }

        // --------------------------------
        // Status validation
        // --------------------------------

        if (booking.getBookingStatus()
                == BookingStatus.CANCELLED) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Cancelled booking cannot be modified");
        }

        if (booking.getBookingStatus()
                == BookingStatus.COMPLETED) {

<<<<<<< HEAD
            throw new RuntimeException(
                    "Completed booking cannot be modified");
        }

=======
            throw new BusinessException(
                    "Completed booking cannot be modified");
        }

        // Once staff have allocated a vehicle and handed it over, the rental
        // is under way and the customer can no longer change its terms.
        if (booking.getAssignedCarId() != null
                || booking.getHandoverDate() != null) {

            throw new BusinessException(
                    "This booking cannot be changed because the vehicle has "
                    + "already been allocated. Please contact the hub.");
        }

        // --------------------------------
        // Date validation
        // --------------------------------

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (!endDate.isAfter(startDate)) {
            throw new BusinessException(
                    "The return date must be after the pick-up date");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new BusinessException(
                    "The pick-up date cannot be in the past");
        }

        // Duration is derived from the dates rather than trusted from the
        // browser, so the two can never disagree.
        int duration = (int) ChronoUnit.DAYS.between(startDate, endDate);
        if (duration < 1) {
            duration = 1;
        }

        // --------------------------------
        // Vehicle type + repricing
        //
        // Every amount is recalculated here from the rate card in the
        // database. Whatever totals the browser sent are ignored, so a
        // modified booking can never be under-priced.
        // --------------------------------

        CarType carType = carTypeRepository
                .findById(request.getCarTypeId().intValue())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "The selected vehicle type is no longer available"));

        if (carType.getHub() == null
                || !carType.getHub().getHubId().equals(request.getPickupHubId())) {

            throw new BusinessException(
                    "That vehicle type is not offered at the selected pick-up hub");
        }

        BigDecimal dailyRate = toAmount(carType.getDailyRate());
        BigDecimal weeklyRate = toAmount(carType.getWeeklyRate());
        BigDecimal monthlyRate = toAmount(carType.getMonthlyRate());

        BigDecimal vehicleAmount =
                calculateVehicleAmount(dailyRate, weeklyRate, monthlyRate, duration);

        BigDecimal addonAmount =
                calculateAddonAmount(request.getAddons(), duration);

        BigDecimal taxAmount = vehicleAmount.add(addonAmount)
                .multiply(TAX_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal grandTotal = vehicleAmount.add(addonAmount).add(taxAmount);

>>>>>>> Developer
        // --------------------------------
        // Update booking
        // --------------------------------

<<<<<<< HEAD
        booking.setStartDate(
                request.getStartDate());

        booking.setEndDate(
                request.getEndDate());
=======
        booking.setCarTypeId(request.getCarTypeId());

        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setDuration(duration);
>>>>>>> Developer

        booking.setPickupHubId(
                request.getPickupHubId());

        booking.setPickupHubName(
                request.getPickupHubName());

        booking.setDropoffHubId(
                request.getDropoffHubId());

        booking.setDropoffHubName(
                request.getDropoffHubName());

<<<<<<< HEAD
        booking.setDuration(
                request.getDuration());

        // Frontend-calculated values
        booking.setVehicleAmount(
                request.getVehicleAmount());

        booking.setAddonAmount(
                request.getAddonAmount());

        booking.setTaxAmount(
                request.getTaxAmount());

        booking.setGrandTotal(
                request.getGrandTotal());
=======
        // Rate snapshot, refreshed for the newly chosen car type
        booking.setDailyRate(dailyRate);
        booking.setWeeklyRate(weeklyRate);
        booking.setMonthlyRate(monthlyRate);

        booking.setVehicleAmount(vehicleAmount);
        booking.setAddonAmount(addonAmount);
        booking.setTaxAmount(taxAmount);
        booking.setGrandTotal(grandTotal);
>>>>>>> Developer

        BookingHeader savedBooking =
                bookingHeaderRepository.save(booking);

        // --------------------------------
        // Update addons
        // --------------------------------

<<<<<<< HEAD
=======
        // Replace the add-on lines. Names and prices are taken from the
        // database so the stored lines always agree with the recalculated
        // addonAmount above.
>>>>>>> Developer
        bookingDetailRepository
                .deleteByBookingId(bookingId);

        if (request.getAddons() != null
                && !request.getAddons().isEmpty()) {

            List<BookingDetail> details =
                    new ArrayList<>();

<<<<<<< HEAD
            for (AddonRequest addon :
                    request.getAddons()) {

                BookingDetail detail =
                        new BookingDetail();
=======
            for (AddonRequest requested : request.getAddons()) {

                if (requested.getAddonId() == null) {
                    continue;
                }

                Addon addon = addonRepository
                        .findById(requested.getAddonId().intValue())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Add-on no longer available"));

                BookingDetail detail = new BookingDetail();
>>>>>>> Developer

                detail.setBookingId(
                        savedBooking.getBookingId());

                detail.setAddonId(
<<<<<<< HEAD
                        addon.getAddonId());
=======
                        requested.getAddonId());
>>>>>>> Developer

                detail.setAddonName(
                        addon.getAddonName());

                detail.setAddonPrice(
<<<<<<< HEAD
                        addon.getAddonPrice());
=======
                        toAmount(addon.getPricePerDay())
                                .multiply(BigDecimal.valueOf(duration)));
>>>>>>> Developer

                detail.setBookingSource(
                        BookingSource.ONLINE);

                details.add(detail);
            }

            bookingDetailRepository.saveAll(details);
        }

        BookingResponse response =
                mapToBookingResponse(savedBooking);

        return new ApiResponse<>(
                true,
                "Booking updated successfully",
                response);
    }
    
    
    @Override
    @Transactional
    public ApiResponse<BookingResponse> cancelBooking(
            Long bookingId) {

        // --------------------------------
        // Get logged-in user
        // --------------------------------

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
                    "User is not authenticated");
        }

        String email = authentication.getName();

        // --------------------------------
        // Find logged-in customer
        // --------------------------------

        Customer customer =
                customerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
                                        "Customer not found"));

        // --------------------------------
        // Find booking
        // --------------------------------

        BookingHeader booking =
                bookingHeaderRepository
                        .findById(bookingId)
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
                                        "Booking not found"));

        // --------------------------------
        // Ownership check
        // --------------------------------

        if (booking.getCustomerId() == null
                || !booking.getCustomerId()
                        .equals(customer.getCustomerId())) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new UnauthorizedActionException(
>>>>>>> Developer
                    "You are not authorized to cancel this booking");
        }

        // --------------------------------
        // Status validation
        // --------------------------------

        if (booking.getBookingStatus()
                == BookingStatus.CANCELLED) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Booking is already cancelled");
        }

        if (booking.getBookingStatus()
                == BookingStatus.COMPLETED) {

<<<<<<< HEAD
            throw new RuntimeException(
=======
            throw new BusinessException(
>>>>>>> Developer
                    "Completed booking cannot be cancelled");
        }

        // --------------------------------
        // Cancel booking
        // --------------------------------

        booking.setBookingStatus(
                BookingStatus.CANCELLED);

        BookingHeader savedBooking =
                bookingHeaderRepository.save(booking);

        BookingResponse response =
                mapToBookingResponse(savedBooking);

        return new ApiResponse<>(
                true,
                "Booking cancelled successfully",
                response);
    }

    
    @Override
    @Transactional(readOnly = true)
    public ApiResponse<BookingStatsResponse> getBookingStats() {

        // --------------------------------
        // Get logged-in user
        // --------------------------------

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
                    "User is not authenticated");
        }

        String email =
                authentication.getName();

        // --------------------------------
        // Find customer
        // --------------------------------

        Customer customer =
                customerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
<<<<<<< HEAD
                                new RuntimeException(
=======
                                new ResourceNotFoundException(
>>>>>>> Developer
                                        "Customer not found"));

        String customerId =
                customer.getCustomerId();

        // --------------------------------
        // Count bookings
        // --------------------------------

        long total =
                bookingHeaderRepository
                        .countByCustomerId(
                                customerId);

        long pending =
                bookingHeaderRepository
                        .countByCustomerIdAndBookingStatus(
                                customerId,
                                BookingStatus.PENDING);

        long confirmed =
                bookingHeaderRepository
                        .countByCustomerIdAndBookingStatus(
                                customerId,
                                BookingStatus.CONFIRMED);

        long cancelled =
                bookingHeaderRepository
                        .countByCustomerIdAndBookingStatus(
                                customerId,
                                BookingStatus.CANCELLED);

        long completed =
                bookingHeaderRepository
                        .countByCustomerIdAndBookingStatus(
                                customerId,
                                BookingStatus.COMPLETED);

        // --------------------------------
        // Create response
        // --------------------------------

        BookingStatsResponse stats =
                new BookingStatsResponse();

        stats.setTotal(total);

        stats.setPending(pending);
        stats.setConfirmed(confirmed);
        stats.setCancelled(cancelled);
        stats.setCompleted(completed);

        // Customer doesn't need today's count currently
        stats.setTodayBookings(0);

        return new ApiResponse<>(
                true,
                "Booking statistics fetched successfully",
                stats);
    }

    
    private boolean isValidStatusTransition(
            BookingStatus currentStatus,
            BookingStatus newStatus) {

        if (currentStatus == null || newStatus == null) {
            return false;
        }

        if (currentStatus == BookingStatus.PENDING) {

            return newStatus == BookingStatus.CONFIRMED
                    || newStatus == BookingStatus.CANCELLED;
        }

        if (currentStatus == BookingStatus.CONFIRMED) {

            return newStatus == BookingStatus.COMPLETED
                    || newStatus == BookingStatus.CANCELLED;
        }

        return false;
    }

 
	@Override
	@Transactional(readOnly = true)
<<<<<<< HEAD
	public ApiResponse<BookingPageResponse> getStaffBookings(int page, int size, BookingStatus status) {
=======
	public ApiResponse<BookingPageResponse> getStaffBookings(
	        int page, int size, BookingStatus status, HubScope scope) {
>>>>>>> Developer
		

	    // --------------------------------
	    // Validate pagination
	    // --------------------------------

	    if (page < 0) {
<<<<<<< HEAD
	        throw new RuntimeException(
=======
	        throw new BusinessException(
>>>>>>> Developer
	                "Page number cannot be negative");
	    }

	    if (size <= 0) {
<<<<<<< HEAD
	        throw new RuntimeException(
=======
	        throw new BusinessException(
>>>>>>> Developer
	                "Page size must be greater than 0");
	    }

	    if (size > 100) {
	        size = 100;
	    }

	    // --------------------------------
	    // Logged-in Staff
	    // --------------------------------

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
	                "Staff is not authenticated");
	    }

	    String email = authentication.getName();

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

	    Integer hubId =
	            staff.getHub().getHubId();

	    // --------------------------------
	    // Pagination
	    // --------------------------------

	    Pageable pageable =
	            PageRequest.of(
	                    page,
	                    size,
	                    Sort.by("createdAt")
	                            .descending());

<<<<<<< HEAD
	    Page<BookingHeader> bookingPage;

	    // --------------------------------
	    // Optional Status Filter
	    // --------------------------------

	    if (status != null) {

	        bookingPage =
	                bookingHeaderRepository
	                        .findByPickupHubIdAndBookingStatus(
	                                hubId,
	                                status,
	                                pageable);

	    } else {

	        bookingPage =
	                bookingHeaderRepository
	                        .findByPickupHubId(
	                                hubId,
	                                pageable);
=======
	    // --------------------------------
	    // Which end of the rental is this hub responsible for?
	    //
	    //   PICKUP -> bookings we hand over   (pickup_hub_id  = my hub)
	    //   RETURN -> bookings we take back   (dropoff_hub_id = my hub)
	    //   ALL    -> anything we touch, either end
	    //
	    // A one-way BOM -> Nagpur rental therefore shows in BOM's Hand-over
	    // module and Nagpur's Return module, and in neither of the others.
	    // --------------------------------

	    HubScope effectiveScope = (scope == null) ? HubScope.ALL : scope;

	    Page<BookingHeader> bookingPage;

	    if (effectiveScope == HubScope.PICKUP) {

	        bookingPage = (status != null)
	                ? bookingHeaderRepository
	                        .findByPickupHubIdAndBookingStatus(hubId, status, pageable)
	                : bookingHeaderRepository
	                        .findByPickupHubId(hubId, pageable);

	    } else if (effectiveScope == HubScope.RETURN) {

	        bookingPage = (status != null)
	                ? bookingHeaderRepository
	                        .findByDropoffHubIdAndBookingStatus(hubId, status, pageable)
	                : bookingHeaderRepository
	                        .findByDropoffHubId(hubId, pageable);

	    } else {

	        bookingPage = bookingHeaderRepository
	                .findByEitherHub(hubId, status, pageable);
>>>>>>> Developer
	    }

	    // --------------------------------
	    // Entity → DTO
	    // --------------------------------

	    List<BookingResponse> bookings =
	            bookingPage
	                    .getContent()
	                    .stream()
	                    .map(this::mapToBookingResponse)
	                    .toList();

	    // --------------------------------
	    // Pagination Response
	    // --------------------------------

	    BookingPageResponse response =
	            new BookingPageResponse();

	    response.setBookings(bookings);

	    response.setCurrentPage(
	            bookingPage.getNumber());

	    response.setPageSize(
	            bookingPage.getSize());

	    response.setTotalElements(
	            bookingPage.getTotalElements());

	    response.setTotalPages(
	            bookingPage.getTotalPages());

	    response.setFirst(
	            bookingPage.isFirst());

	    response.setLast(
	            bookingPage.isLast());

	    return new ApiResponse<>(
	            true,
	            "Staff bookings fetched successfully",
	            response);
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public ApiResponse<BookingResponse> getStaffBookingById(
	        Long bookingId) {

	    // ----------------------------
	    // Logged-in Staff
	    // ----------------------------

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
	                "Staff is not authenticated");
	    }

	    String email = authentication.getName();

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

	    Integer hubId =
	            staff.getHub().getHubId();

	    // ----------------------------
	    // Find Booking
	    // ----------------------------

	    BookingHeader booking =
	            bookingHeaderRepository
	                    .findById(bookingId)
	                    .orElseThrow(() ->
<<<<<<< HEAD
	                            new RuntimeException(
=======
	                            new ResourceNotFoundException(
>>>>>>> Developer
	                                    "Booking not found"));

	    // ----------------------------
	    // Hub Authorization
	    // ----------------------------

	    if (!hubId.equals(
	            booking.getPickupHubId())) {

<<<<<<< HEAD
	        throw new RuntimeException(
=======
	        throw new UnauthorizedActionException(
>>>>>>> Developer
	                "You are not authorized to access this booking");
	    }

	    BookingResponse response =
	            mapToBookingResponse(booking);

	    return new ApiResponse<>(
	            true,
	            "Booking fetched successfully",
	            response);
	}
	
	@Override
	@Transactional(readOnly = true)
	public ApiResponse<BookingStatsResponse> getStaffBookingStats() {

	    //-----------------------------------
	    // Logged-in Staff
	    //-----------------------------------

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
	                "Staff is not authenticated");
	    }

	    String email = authentication.getName();

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

	    Integer hubId =
	            staff.getHub().getHubId();

	    //-----------------------------------
	    // Today's range
	    //-----------------------------------

	    LocalDateTime startOfDay =
	            LocalDate.now().atStartOfDay();

	    LocalDateTime endOfDay =
	            startOfDay.plusDays(1);

	    //-----------------------------------
	    // Counts
	    //-----------------------------------

<<<<<<< HEAD
	    long total =
	            bookingHeaderRepository
	                    .countByPickupHubId(hubId);

=======
	    // Anything this hub touches at either end.
	    long total =
	            bookingHeaderRepository
	                    .countByEitherHub(hubId, null);

	    // Waiting to be handed over BY US - so pickup hub.
>>>>>>> Developer
	    long pending =
	            bookingHeaderRepository
	                    .countByPickupHubIdAndBookingStatus(
	                            hubId,
	                            BookingStatus.PENDING);

<<<<<<< HEAD
	    long confirmed =
	            bookingHeaderRepository
	                    .countByPickupHubIdAndBookingStatus(
=======
	    // Out on rent and coming back TO US - so drop-off hub.
	    long confirmed =
	            bookingHeaderRepository
	                    .countByDropoffHubIdAndBookingStatus(
>>>>>>> Developer
	                            hubId,
	                            BookingStatus.CONFIRMED);

	    long completed =
	            bookingHeaderRepository
<<<<<<< HEAD
	                    .countByPickupHubIdAndBookingStatus(
	                            hubId,
	                            BookingStatus.COMPLETED);

	    long cancelled =
	            bookingHeaderRepository
	                    .countByPickupHubIdAndBookingStatus(
	                            hubId,
	                            BookingStatus.CANCELLED);
=======
	                    .countByEitherHub(hubId, BookingStatus.COMPLETED);

	    long cancelled =
	            bookingHeaderRepository
	                    .countByEitherHub(hubId, BookingStatus.CANCELLED);
>>>>>>> Developer

	    long todayBookings =
	            bookingHeaderRepository
	                    .countByPickupHubIdAndCreatedAtBetween(
	                            hubId,
	                            startOfDay,
	                            endOfDay);

	    //-----------------------------------
	    // Response
	    //-----------------------------------

	    BookingStatsResponse response =
	            new BookingStatsResponse();

	    response.setTotal(total);
	    response.setTodayBookings(todayBookings);

	    response.setPending(pending);
	    response.setConfirmed(confirmed);
	    response.setCompleted(completed);
	    response.setCancelled(cancelled);

	    return new ApiResponse<>(
	            true,
	            "Staff booking statistics fetched successfully",
	            response);
	}
	
	
	@Override
	@Transactional
	public ApiResponse<BookingResponse> updateStaffBookingStatus(
	        Long bookingId,
	        UpdateBookingStatusRequest request) {

	    //--------------------------------
	    // Logged-in Staff
	    //--------------------------------

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
	                "Staff is not authenticated");
	    }

	    String email = authentication.getName();

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

	    Integer hubId =
	            staff.getHub().getHubId();

	    //--------------------------------
	    // Find Booking
	    //--------------------------------

	    BookingHeader booking =
	            bookingHeaderRepository
	                    .findById(bookingId)
	                    .orElseThrow(() ->
<<<<<<< HEAD
	                            new RuntimeException(
=======
	                            new ResourceNotFoundException(
>>>>>>> Developer
	                                    "Booking not found"));

	    //--------------------------------
	    // Hub Authorization
	    //--------------------------------

	    if (!hubId.equals(
	            booking.getPickupHubId())) {

<<<<<<< HEAD
	        throw new RuntimeException(
=======
	        throw new UnauthorizedActionException(
>>>>>>> Developer
	                "You are not authorized to update this booking");
	    }

	    //--------------------------------
	    // Status Validation
	    //--------------------------------

	    BookingStatus newStatus =
	            request.getStatus();

	    if (newStatus == null) {

<<<<<<< HEAD
	        throw new RuntimeException(
=======
	        throw new BusinessException(
>>>>>>> Developer
	                "Booking status is required");
	    }

	    BookingStatus currentStatus =
	            booking.getBookingStatus();

	    if (!isValidStatusTransition(
	            currentStatus,
	            newStatus)) {

<<<<<<< HEAD
	        throw new RuntimeException(
=======
	        throw new BusinessException(
>>>>>>> Developer
	                "Invalid booking status transition from "
	                        + currentStatus
	                        + " to "
	                        + newStatus);
	    }

	    //--------------------------------
	    // Update
	    //--------------------------------

	    booking.setBookingStatus(
	            newStatus);

	    BookingHeader savedBooking =
	            bookingHeaderRepository
	                    .save(booking);

	    BookingResponse response =
	            mapToBookingResponse(savedBooking);

	    return new ApiResponse<>(
	            true,
	            "Booking status updated successfully",
	            response);
	}
	
	
	@Override
	@Transactional
	public ApiResponse<BookingResponse> cancelStaffBooking(
	        Long bookingId) {

	    //--------------------------------
	    // Logged-in Staff
	    //--------------------------------

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
	                "Staff is not authenticated");
	    }

	    String email = authentication.getName();

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

	    Integer hubId =
	            staff.getHub().getHubId();

	    //--------------------------------
	    // Booking
	    //--------------------------------

	    BookingHeader booking =
	            bookingHeaderRepository
	                    .findById(bookingId)
	                    .orElseThrow(() ->
<<<<<<< HEAD
	                            new RuntimeException(
=======
	                            new ResourceNotFoundException(
>>>>>>> Developer
	                                    "Booking not found"));

	    //--------------------------------
	    // Hub Validation
	    //--------------------------------

	    if (!hubId.equals(
	            booking.getPickupHubId())) {

<<<<<<< HEAD
	        throw new RuntimeException(
=======
	        throw new UnauthorizedActionException(
>>>>>>> Developer
	                "You are not authorized to cancel this booking");
	    }

	    //--------------------------------
	    // Validation
	    //--------------------------------

	    if (booking.getBookingStatus()
	            == BookingStatus.CANCELLED) {

<<<<<<< HEAD
	        throw new RuntimeException(
=======
	        throw new BusinessException(
>>>>>>> Developer
	                "Booking is already cancelled");
	    }

	    if (booking.getBookingStatus()
	            == BookingStatus.COMPLETED) {

<<<<<<< HEAD
	        throw new RuntimeException(
=======
	        throw new BusinessException(
>>>>>>> Developer
	                "Completed booking cannot be cancelled");
	    }

	    //--------------------------------
	    // Cancel
	    //--------------------------------

	    booking.setBookingStatus(
	            BookingStatus.CANCELLED);

	    BookingHeader savedBooking =
	            bookingHeaderRepository.save(booking);

	    BookingResponse response =
	            mapToBookingResponse(savedBooking);

	    return new ApiResponse<>(
	            true,
	            "Booking cancelled successfully",
	            response);
	}
    
	}
	

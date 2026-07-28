package com.example.demo.service.impl;

import java.time.LocalDate;
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
import com.example.demo.dto.request.UpdateBookingRequest;
import com.example.demo.dto.request.UpdateBookingStatusRequest;
import com.example.demo.dto.response.BookingPageResponse;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.dto.response.BookingStatsResponse;
import com.example.demo.entity.BookingDetail;
import com.example.demo.entity.BookingHeader;
import com.example.demo.entity.base.Customer;
import com.example.demo.entity.base.Staff;
import com.example.demo.enums.BookingSource;
import com.example.demo.enums.BookingStatus;
import com.example.demo.repository.BookingDetailRepository;
import com.example.demo.repository.BookingHeaderRepository;
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

    public BookingServiceImpl(
            BookingHeaderRepository bookingHeaderRepository,
            BookingDetailRepository bookingDetailRepository,
            CustomerRepository customerRepository,
            StaffRepository staffRepository,
            EmailService emailService) {

        this.bookingHeaderRepository = bookingHeaderRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
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

    
    private BookingResponse mapToBookingResponse(
            BookingHeader booking) {

        BookingResponse response =
                new BookingResponse();

        response.setBookingId(
                booking.getBookingId());

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

        response.setCarId(
                booking.getCarId());

        response.setPickupHubId(
                booking.getPickupHubId());

        response.setPickupHubName(
                booking.getPickupHubName());

        response.setDropoffHubId(
                booking.getDropoffHubId());

        response.setDropoffHubName(
                booking.getDropoffHubName());

        response.setStartDate(
                booking.getStartDate());

        response.setEndDate(
                booking.getEndDate());

        response.setDuration(
                booking.getDuration());

        response.setVehicleAmount(
                booking.getVehicleAmount());

        response.setAddonAmount(
                booking.getAddonAmount());

        response.setTaxAmount(
                booking.getTaxAmount());

        response.setGrandTotal(
                booking.getGrandTotal());

        response.setBookingStatus(
                booking.getBookingStatus());

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
            throw new RuntimeException(
                    "Page number cannot be negative");
        }

        if (size <= 0) {
            throw new RuntimeException(
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

            throw new RuntimeException(
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
                                new RuntimeException(
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

            throw new RuntimeException(
                    "User is not authenticated");
        }

        String email = authentication.getName();

        // Find logged-in customer
        Customer customer =
                customerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found"));

        // Find booking
        BookingHeader booking =
                bookingHeaderRepository
                        .findById(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found"));

        // Ownership check
        if (booking.getCustomerId() == null
                || !booking.getCustomerId()
                        .equals(customer.getCustomerId())) {

            throw new RuntimeException(
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

            throw new RuntimeException(
                    "User is not authenticated");
        }

        String email = authentication.getName();

        Customer customer =
                customerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found"));

        // --------------------------------
        // Find booking
        // --------------------------------

        BookingHeader booking =
                bookingHeaderRepository
                        .findById(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found"));

        // --------------------------------
        // Ownership check
        // --------------------------------

        if (booking.getCustomerId() == null
                || !booking.getCustomerId()
                        .equals(customer.getCustomerId())) {

            throw new RuntimeException(
                    "You are not authorized to modify this booking");
        }

        // --------------------------------
        // Status validation
        // --------------------------------

        if (booking.getBookingStatus()
                == BookingStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cancelled booking cannot be modified");
        }

        if (booking.getBookingStatus()
                == BookingStatus.COMPLETED) {

            throw new RuntimeException(
                    "Completed booking cannot be modified");
        }

        // --------------------------------
        // Update booking
        // --------------------------------

        booking.setStartDate(
                request.getStartDate());

        booking.setEndDate(
                request.getEndDate());

        booking.setPickupHubId(
                request.getPickupHubId());

        booking.setPickupHubName(
                request.getPickupHubName());

        booking.setDropoffHubId(
                request.getDropoffHubId());

        booking.setDropoffHubName(
                request.getDropoffHubName());

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

        BookingHeader savedBooking =
                bookingHeaderRepository.save(booking);

        // --------------------------------
        // Update addons
        // --------------------------------

        bookingDetailRepository
                .deleteByBookingId(bookingId);

        if (request.getAddons() != null
                && !request.getAddons().isEmpty()) {

            List<BookingDetail> details =
                    new ArrayList<>();

            for (AddonRequest addon :
                    request.getAddons()) {

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

            throw new RuntimeException(
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
                                new RuntimeException(
                                        "Customer not found"));

        // --------------------------------
        // Find booking
        // --------------------------------

        BookingHeader booking =
                bookingHeaderRepository
                        .findById(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found"));

        // --------------------------------
        // Ownership check
        // --------------------------------

        if (booking.getCustomerId() == null
                || !booking.getCustomerId()
                        .equals(customer.getCustomerId())) {

            throw new RuntimeException(
                    "You are not authorized to cancel this booking");
        }

        // --------------------------------
        // Status validation
        // --------------------------------

        if (booking.getBookingStatus()
                == BookingStatus.CANCELLED) {

            throw new RuntimeException(
                    "Booking is already cancelled");
        }

        if (booking.getBookingStatus()
                == BookingStatus.COMPLETED) {

            throw new RuntimeException(
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

            throw new RuntimeException(
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
                                new RuntimeException(
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
	public ApiResponse<BookingPageResponse> getStaffBookings(int page, int size, BookingStatus status) {
		

	    // --------------------------------
	    // Validate pagination
	    // --------------------------------

	    if (page < 0) {
	        throw new RuntimeException(
	                "Page number cannot be negative");
	    }

	    if (size <= 0) {
	        throw new RuntimeException(
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

	        throw new RuntimeException(
	                "Staff is not authenticated");
	    }

	    String email = authentication.getName();

	    Staff staff =
	            staffRepository
	                    .findByEmail(email)
	                    .orElseThrow(() ->
	                            new RuntimeException(
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

	        throw new RuntimeException(
	                "Staff is not authenticated");
	    }

	    String email = authentication.getName();

	    Staff staff =
	            staffRepository
	                    .findByEmail(email)
	                    .orElseThrow(() ->
	                            new RuntimeException(
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
	                            new RuntimeException(
	                                    "Booking not found"));

	    // ----------------------------
	    // Hub Authorization
	    // ----------------------------

	    if (!hubId.equals(
	            booking.getPickupHubId())) {

	        throw new RuntimeException(
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

	        throw new RuntimeException(
	                "Staff is not authenticated");
	    }

	    String email = authentication.getName();

	    Staff staff =
	            staffRepository
	                    .findByEmail(email)
	                    .orElseThrow(() ->
	                            new RuntimeException(
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

	    long total =
	            bookingHeaderRepository
	                    .countByPickupHubId(hubId);

	    long pending =
	            bookingHeaderRepository
	                    .countByPickupHubIdAndBookingStatus(
	                            hubId,
	                            BookingStatus.PENDING);

	    long confirmed =
	            bookingHeaderRepository
	                    .countByPickupHubIdAndBookingStatus(
	                            hubId,
	                            BookingStatus.CONFIRMED);

	    long completed =
	            bookingHeaderRepository
	                    .countByPickupHubIdAndBookingStatus(
	                            hubId,
	                            BookingStatus.COMPLETED);

	    long cancelled =
	            bookingHeaderRepository
	                    .countByPickupHubIdAndBookingStatus(
	                            hubId,
	                            BookingStatus.CANCELLED);

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

	        throw new RuntimeException(
	                "Staff is not authenticated");
	    }

	    String email = authentication.getName();

	    Staff staff =
	            staffRepository
	                    .findByEmail(email)
	                    .orElseThrow(() ->
	                            new RuntimeException(
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
	                            new RuntimeException(
	                                    "Booking not found"));

	    //--------------------------------
	    // Hub Authorization
	    //--------------------------------

	    if (!hubId.equals(
	            booking.getPickupHubId())) {

	        throw new RuntimeException(
	                "You are not authorized to update this booking");
	    }

	    //--------------------------------
	    // Status Validation
	    //--------------------------------

	    BookingStatus newStatus =
	            request.getStatus();

	    if (newStatus == null) {

	        throw new RuntimeException(
	                "Booking status is required");
	    }

	    BookingStatus currentStatus =
	            booking.getBookingStatus();

	    if (!isValidStatusTransition(
	            currentStatus,
	            newStatus)) {

	        throw new RuntimeException(
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

	        throw new RuntimeException(
	                "Staff is not authenticated");
	    }

	    String email = authentication.getName();

	    Staff staff =
	            staffRepository
	                    .findByEmail(email)
	                    .orElseThrow(() ->
	                            new RuntimeException(
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
	                            new RuntimeException(
	                                    "Booking not found"));

	    //--------------------------------
	    // Hub Validation
	    //--------------------------------

	    if (!hubId.equals(
	            booking.getPickupHubId())) {

	        throw new RuntimeException(
	                "You are not authorized to cancel this booking");
	    }

	    //--------------------------------
	    // Validation
	    //--------------------------------

	    if (booking.getBookingStatus()
	            == BookingStatus.CANCELLED) {

	        throw new RuntimeException(
	                "Booking is already cancelled");
	    }

	    if (booking.getBookingStatus()
	            == BookingStatus.COMPLETED) {

	        throw new RuntimeException(
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
	

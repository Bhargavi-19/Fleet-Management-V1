package com.example.demo.service.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.demo.dto.response.InvoiceAddonResponse;
import com.example.demo.dto.response.InvoicePageResponse;
import com.example.demo.dto.response.InvoiceResponse;
import com.example.demo.entity.InvoiceHeader;
import com.example.demo.entity.base.CarType;
import com.example.demo.entity.base.Customer;
import com.example.demo.entity.base.Staff;
import com.example.demo.exception.error.ResourceNotFoundException;
import com.example.demo.exception.error.UnauthorizedActionException;
import com.example.demo.repository.CarTypeRepository;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.StateRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.InvoiceDetailRepository;
import com.example.demo.repository.InvoiceHeaderRepository;
import com.example.demo.repository.StaffRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.InvoiceService;
import com.example.demo.service.PdfService;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceHeaderRepository invoiceHeaderRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final CustomerRepository customerRepository;
    private final CarTypeRepository carTypeRepository;
    private final StaffRepository staffRepository;
    private final CityRepository cityRepository;
    private final StateRepository stateRepository;
    private final PdfService pdfService;

    public InvoiceServiceImpl(
            InvoiceHeaderRepository invoiceHeaderRepository,
            InvoiceDetailRepository invoiceDetailRepository,
            CustomerRepository customerRepository,
            CarTypeRepository carTypeRepository,
            StaffRepository staffRepository,
            CityRepository cityRepository,
            StateRepository stateRepository,
            PdfService pdfService) {

        this.invoiceHeaderRepository = invoiceHeaderRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
        this.customerRepository = customerRepository;
        this.carTypeRepository = carTypeRepository;
        this.staffRepository = staffRepository;
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
        this.pdfService = pdfService;
    }

    // =====================================================
    // Public API - always ownership checked
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<InvoiceResponse> getInvoiceByBookingId(Long bookingId) {

        InvoiceResponse invoice = build(bookingId, true);

        return new ApiResponse<>(
                true,
                "Invoice fetched successfully",
                invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadInvoicePdf(Long bookingId) {

        return pdfService.generateInvoicePdf(build(bookingId, true));
    }

    // =====================================================
    // Staff API - scoped to the staff member's hub
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<InvoicePageResponse> getStaffInvoices(
            int page, int size, String search) {

        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 100) size = 100;

        Integer hubId = getLoggedInStaffHubId();

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("invoiceDate").descending());

        Page<InvoiceHeader> found =
                invoiceHeaderRepository.searchByHub(hubId, search, pageable);

        InvoicePageResponse response = new InvoicePageResponse();
        response.setInvoices(found.getContent().stream()
                .map(this::decorate)
                .toList());
        response.setCurrentPage(found.getNumber());
        response.setPageSize(found.getSize());
        response.setTotalElements(found.getTotalElements());
        response.setTotalPages(found.getTotalPages());
        response.setFirst(found.isFirst());
        response.setLast(found.isLast());

        return new ApiResponse<>(true, "Invoices fetched successfully", response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<InvoiceResponse> getStaffInvoice(Long invoiceId) {

        return new ApiResponse<>(
                true,
                "Invoice fetched successfully",
                decorate(loadForStaff(invoiceId)));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadStaffInvoicePdf(Long invoiceId) {

        return pdfService.generateInvoicePdf(decorate(loadForStaff(invoiceId)));
    }

    /** Finds an invoice and checks it belongs to the staff member's hub. */
    private InvoiceHeader loadForStaff(Long invoiceId) {

        InvoiceHeader invoice = invoiceHeaderRepository
                .findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        Integer hubId = getLoggedInStaffHubId();

        // Either end of the rental may open it: the hub that handed the car
        // over and the hub that took it back both have a legitimate interest.
        boolean isPickupHub = hubId.equals(invoice.getPickupHubId());
        boolean isDropoffHub = hubId.equals(invoice.getDropoffHubId());

        if (!isPickupHub && !isDropoffHub) {

            throw new UnauthorizedActionException(
                    "This invoice belongs to another hub");
        }

        return invoice;
    }

    private Integer getLoggedInStaffHubId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedActionException("Staff is not authenticated");
        }

        Staff staff = staffRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        return staff.getHub().getHubId();
    }

    // =====================================================
    // Internal - no ownership check
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse loadInvoiceForBooking(Long bookingId) {

        return build(bookingId, false);
    }

    // =====================================================
    // Helpers
    // =====================================================

    /**
     * Builds the full invoice DTO.
     *
     * @param checkOwner true when a customer is asking for it, so we make sure
     *                   the invoice actually belongs to them.
     */
    private InvoiceResponse build(Long bookingId, boolean checkOwner) {

        InvoiceHeader invoice = invoiceHeaderRepository
                .findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No invoice has been raised for this booking yet."));

        if (checkOwner) {
            assertBelongsToLoggedInCustomer(invoice);
        }

        return decorate(invoice);
    }

    /** Turns an invoice entity into the full DTO: add-on lines + car type name. */
    private InvoiceResponse decorate(InvoiceHeader invoice) {

        InvoiceResponse response = InvoiceResponse.fromEntity(invoice);

        List<InvoiceAddonResponse> addons = invoiceDetailRepository
                .findByInvoiceId(invoice.getInvoiceId())
                .stream()
                .map(InvoiceAddonResponse::fromEntity)
                .toList();

        response.setAddons(addons);

        if (invoice.getCityId() != null) {
            cityRepository.findById(invoice.getCityId())
                    .ifPresent(c -> response.setCityName(c.getCityName()));
        }
        if (invoice.getStateId() != null) {
            stateRepository.findById(invoice.getStateId())
                    .ifPresent(st -> response.setStateName(st.getStateName()));
        }

        if (invoice.getCarTypeId() != null) {
            carTypeRepository
                    .findById(invoice.getCarTypeId().intValue())
                    .map(CarType::getCarType)
                    .ifPresent(response::setCarTypeName);
        }

        return response;
    }

    /** A customer may only open their own invoice. */
    private void assertBelongsToLoggedInCustomer(InvoiceHeader invoice) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedActionException("You are not authenticated");
        }

        Customer customer = customerRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (invoice.getCustomerId() == null
                || !invoice.getCustomerId().equals(customer.getCustomerId())) {

            throw new UnauthorizedActionException(
                    "You are not authorized to view this invoice");
        }
    }
}

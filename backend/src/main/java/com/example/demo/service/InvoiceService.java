package com.example.demo.service;

import com.example.demo.dto.response.InvoicePageResponse;
import com.example.demo.dto.response.InvoiceResponse;
import com.example.demo.response.ApiResponse;

public interface InvoiceService {

    /** Invoice for one booking, for the logged-in customer. */
    ApiResponse<InvoiceResponse> getInvoiceByBookingId(Long bookingId);

    /** Same invoice rendered as a PDF, for the download button. */
    byte[] downloadInvoicePdf(Long bookingId);

    /** Paginated, searchable invoices for the logged-in staff member's hub. */
    ApiResponse<InvoicePageResponse> getStaffInvoices(int page, int size, String search);

    /** One invoice for staff, checked against their hub. */
    ApiResponse<InvoiceResponse> getStaffInvoice(Long invoiceId);

    /** Staff PDF download, checked against their hub. */
    byte[] downloadStaffInvoicePdf(Long invoiceId);

    /**
     * Loads an invoice without any ownership check.
     * Used internally right after a return, when there is no logged-in
     * customer in context - only staff.
     */
    InvoiceResponse loadInvoiceForBooking(Long bookingId);
}

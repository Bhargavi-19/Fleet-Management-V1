package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.InvoiceResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.InvoiceService;

/**
 * Customer facing invoice endpoints.
 *
 * Both are protected: a customer can only reach their own invoices. The
 * ownership check lives in InvoiceServiceImpl.
 */
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /** Invoice for a booking, as JSON, for the Invoice page. */
    @GetMapping("/booking/{bookingId}")
    public ApiResponse<InvoiceResponse> getInvoice(
            @PathVariable Long bookingId) {

        return invoiceService.getInvoiceByBookingId(bookingId);
    }

    /** The same invoice as a PDF, for the Download button. */
    @GetMapping("/booking/{bookingId}/download")
    public ResponseEntity<byte[]> downloadInvoice(
            @PathVariable Long bookingId) {

        byte[] pdf = invoiceService.downloadInvoicePdf(bookingId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "attachment",
                "Invoice-Booking-" + bookingId + ".pdf");
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, org.springframework.http.HttpStatus.OK);
    }
}

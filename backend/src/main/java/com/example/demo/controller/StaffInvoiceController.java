package com.example.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.InvoicePageResponse;
import com.example.demo.dto.response.InvoiceResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.InvoiceService;

/**
 * Invoices for the staff Invoices screen.
 *
 * Sits under /api/staff so the existing security rules restrict it to
 * ROLE_STAFF. Every result is scoped to the staff member's own hub.
 */
@RestController
@RequestMapping("/api/staff/invoices")
public class StaffInvoiceController {

    private final InvoiceService invoiceService;

    public StaffInvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * Paginated invoices for the hub.
     *
     * `search` matches invoice number, booking id, customer name, e-mail or
     * vehicle registration.
     */
    @GetMapping
    public ApiResponse<InvoicePageResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        return invoiceService.getStaffInvoices(page, size, search);
    }

    @GetMapping("/{invoiceId}")
    public ApiResponse<InvoiceResponse> get(@PathVariable Long invoiceId) {

        return invoiceService.getStaffInvoice(invoiceId);
    }

    @GetMapping("/{invoiceId}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long invoiceId) {

        byte[] pdf = invoiceService.downloadStaffInvoicePdf(invoiceId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "attachment", "Invoice-" + invoiceId + ".pdf");
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}

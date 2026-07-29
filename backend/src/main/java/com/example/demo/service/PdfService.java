package com.example.demo.service;

import com.example.demo.dto.response.BookingResponse;
import com.example.demo.dto.response.InvoiceResponse;

public interface PdfService {

    /** Confirmation sent when a booking is made. */
    byte[] generateBookingPdf(
            BookingResponse booking);

    /** Final invoice, produced once the vehicle has been returned. */
    byte[] generateInvoicePdf(
            InvoiceResponse invoice);
}
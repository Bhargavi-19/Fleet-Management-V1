package com.example.demo.service.impl;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.BookingResponse;
import com.example.demo.service.PdfService;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class PdfServiceImpl implements PdfService {

    @Override
    public byte[] generateBookingPdf(
            BookingResponse booking) {

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(
                    document,
                    outputStream);

            document.open();

            // --------------------------------
            // Title
            // --------------------------------

            Font titleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            20);

            Paragraph title =
                    new Paragraph(
                            "Booking Confirmation",
                            titleFont);

            title.setAlignment(
                    Paragraph.ALIGN_CENTER);

            document.add(title);

            document.add(
                    new Paragraph("\n"));

            // --------------------------------
            // Customer information
            // --------------------------------

            Font headingFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            14);

            document.add(
                    new Paragraph(
                            "Customer Details",
                            headingFont));

            document.add(
                    new Paragraph(
                            "Name: "
                            + booking.getCustomerName()));

            document.add(
                    new Paragraph(
                            "Email: "
                            + booking.getEmail()));

            document.add(
                    new Paragraph("\n"));

            // --------------------------------
            // Booking details
            // --------------------------------

            document.add(
                    new Paragraph(
                            "Booking Details",
                            headingFont));

            PdfPTable bookingTable =
                    new PdfPTable(2);

            bookingTable.setWidthPercentage(100);

            bookingTable.addCell("Booking ID");
            bookingTable.addCell(
                    String.valueOf(
                            booking.getBookingId()));

            bookingTable.addCell("Car ID");
            bookingTable.addCell(
                    String.valueOf(
                            booking.getCarId()));

            bookingTable.addCell("Pickup Location");
            bookingTable.addCell(
                    String.valueOf(
                            booking.getPickupHubName()));

            bookingTable.addCell("Drop-off Location");
            bookingTable.addCell(
                    String.valueOf(
                            booking.getDropoffHubName()));

            bookingTable.addCell("Start Date");
            bookingTable.addCell(
                    String.valueOf(
                            booking.getStartDate()));

            bookingTable.addCell("End Date");
            bookingTable.addCell(
                    String.valueOf(
                            booking.getEndDate()));

            bookingTable.addCell("Duration");
            bookingTable.addCell(
                    booking.getDuration()
                    + " days");

            bookingTable.addCell("Status");
            bookingTable.addCell(
                    String.valueOf(
                            booking.getBookingStatus()));

            document.add(bookingTable);

            document.add(
                    new Paragraph("\n"));

            // --------------------------------
            // Payment details
            // --------------------------------

            document.add(
                    new Paragraph(
                            "Payment Details",
                            headingFont));

            PdfPTable paymentTable =
                    new PdfPTable(2);

            paymentTable.setWidthPercentage(100);

            paymentTable.addCell(
                    "Vehicle Amount");

            paymentTable.addCell(
                    "Rs. "
                    + booking.getVehicleAmount());

            paymentTable.addCell(
                    "Add-on Amount");

            paymentTable.addCell(
                    "Rs. "
                    + booking.getAddonAmount());

            paymentTable.addCell(
                    "Tax Amount");

            paymentTable.addCell(
                    "Rs. "
                    + booking.getTaxAmount());

            paymentTable.addCell(
                    "Grand Total");

            paymentTable.addCell(
                    "Rs. "
                    + booking.getGrandTotal());

            document.add(paymentTable);

            document.add(
                    new Paragraph("\n"));

            document.add(
                    new Paragraph(
                            "Thank you for choosing our Fleet Management service."));

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate booking PDF",
                    e);
        }
    }
}
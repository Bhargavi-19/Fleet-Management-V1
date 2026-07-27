package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.demo.dto.response.BookingResponse;
import com.example.demo.service.EmailService;
import com.example.demo.service.PdfService;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final PdfService pdfService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            PdfService pdfService) {

        this.mailSender = mailSender;
        this.pdfService = pdfService;
    }

    @Override
    public void sendBookingConfirmation(
            BookingResponse booking) {

        try {

            // 1. Generate booking PDF
            byte[] pdfBytes =
                    pdfService.generateBookingPdf(booking);

            // 2. Create MIME email
            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8");

            // 3. Email information
            helper.setFrom(fromEmail);
            helper.setTo(booking.getEmail());

            helper.setSubject(
                    "Booking Confirmation - #"
                    + booking.getBookingId());

            // 4. Email body
            String body =
                    "Hello "
                    + booking.getCustomerName()
                    + ",\n\n"

                    + "Your booking has been confirmed successfully.\n\n"

                    + "Booking ID: "
                    + booking.getBookingId()
                    + "\n"

                    + "Pickup: "
                    + booking.getPickupHubName()
                    + "\n"

                    + "Drop-off: "
                    + booking.getDropoffHubName()
                    + "\n"

                    + "Start Date: "
                    + booking.getStartDate()
                    + "\n"

                    + "End Date: "
                    + booking.getEndDate()
                    + "\n"

                    + "Duration: "
                    + booking.getDuration()
                    + " days\n\n"

                    + "Vehicle Amount: ₹"
                    + booking.getVehicleAmount()
                    + "\n"

                    + "Add-on Amount: ₹"
                    + booking.getAddonAmount()
                    + "\n"

                    + "Tax: ₹"
                    + booking.getTaxAmount()
                    + "\n"

                    + "Grand Total: ₹"
                    + booking.getGrandTotal()
                    + "\n\n"

                    + "Your booking confirmation PDF "
                    + "is attached to this email.\n\n"

                    + "Thank you for choosing our "
                    + "Fleet Management service.";

            helper.setText(body);

            // 5. Attach PDF
            String fileName =
                    "Booking-"
                    + booking.getBookingId()
                    + ".pdf";

            ByteArrayResource pdfResource =
                    new ByteArrayResource(pdfBytes);

            helper.addAttachment(
                    fileName,
                    pdfResource);

            // 6. Send email
            mailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send booking confirmation email",
                    e);
        }
    }
}
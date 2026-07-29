package com.example.demo.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.demo.dto.response.BookingResponse;
import com.example.demo.dto.response.InvoiceResponse;
import com.example.demo.service.EmailService;
import com.example.demo.service.PdfService;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final PdfService pdfService;

    // Falls back to a placeholder so the application still starts when
    // no mail account is configured (e.g. on a fresh developer machine).
    @Value("${spring.mail.username:no-reply@fleman.local}")
    private String fromEmail;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            PdfService pdfService) {

        this.mailSender = mailSender;
        this.pdfService = pdfService;
    }

    /**
     * Runs on a background thread.
     *
     * Generating the PDF and talking to the SMTP server takes seconds, and the
     * customer should not sit on a spinner waiting for it. The booking is
     * already saved by the time this starts, so a slow or unreachable mail
     * server cannot hold up (or undo) the booking.
     */
    @Async
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

            // 4. Email body - same HTML design as the invoice e-mail
            helper.setText(EmailTemplates.bookingEmail(booking), true);

            // 5. Attach PDF
            helper.addAttachment(
                    "Booking-" + booking.getBookingId() + ".pdf",
                    new ByteArrayResource(pdfBytes));

            // 6. Send email
            mailSender.send(message);

        } catch (Exception e) {

            // The booking itself is already saved. A mail server that is
            // down or misconfigured must not roll it back, so we log the
            // problem and let the booking succeed.
            log.error(
                    "Could not send booking confirmation email for booking {}: {}",
                    booking.getBookingId(),
                    e.getMessage());
        }
    }

    /**
     * Sends the final invoice once the vehicle has been returned.
     *
     * Runs on a background thread and never throws: the return is already
     * complete and committed by this point, so a mail problem must only be
     * logged, never allowed to fail the return.
     */
    @Async
    @Override
    public void sendInvoice(InvoiceResponse invoice) {

        try {

            byte[] pdfBytes = pdfService.generateInvoicePdf(invoice);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(invoice.getEmail());
            helper.setSubject("Your FLEMAN invoice " + invoice.getInvoiceNo());

            // true = this body is HTML. Clients that cannot render HTML fall
            // back to stripping the tags, so the content is still readable.
            helper.setText(EmailTemplates.invoiceEmail(invoice), true);

            helper.addAttachment(
                    "Invoice-" + invoice.getInvoiceNo() + ".pdf",
                    new ByteArrayResource(pdfBytes));

            mailSender.send(message);

            log.info("Invoice email sent for booking {} to {}",
                    invoice.getBookingId(), invoice.getEmail());

        } catch (Exception e) {
            log.error("Could not send invoice email for booking {}: {}",
                    invoice.getBookingId(), e.getMessage());
        }
    }
}

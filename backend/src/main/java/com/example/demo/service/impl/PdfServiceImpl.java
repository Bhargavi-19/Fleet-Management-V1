package com.example.demo.service.impl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.BookingAddonResponse;
import com.example.demo.dto.response.InvoiceAddonResponse;
import com.example.demo.dto.response.InvoiceResponse;
import com.example.demo.exception.error.BusinessException;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.service.PdfService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

@Service
public class PdfServiceImpl implements PdfService {

    @Override
    public byte[] generateBookingPdf(BookingResponse booking) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font label = FontFactory.getFont(FontFactory.HELVETICA, 9, MUTED);
            Font value = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font cell = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Font smallMuted = FontFactory.getFont(FontFactory.HELVETICA, 8, MUTED);

            // ---- Branded header, identical to the invoice ----
            addBrandHeader(document, "BOOKING CONFIRMATION",
                    "Booking Ref: #" + text(booking.getBookingId()) + "\n"
                            + "Booked on: " + dateTime(booking.getBookingDate()) + "\n"
                            + "Status: " + text(booking.getBookingStatus()));

            // ---- Confirmation banner ----
            PdfPTable banner = new PdfPTable(1);
            banner.setWidthPercentage(100);
            banner.setSpacingBefore(14f);
            PdfPCell bannerCell = new PdfPCell(new Phrase(
                    "Your booking is confirmed. Please carry your driving licence "
                    + "and a photo ID when collecting the vehicle.",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BRAND)));
            bannerCell.setBackgroundColor(new Color(219, 234, 254));   // blue-100
            bannerCell.setPadding(11f);
            bannerCell.setBorder(Rectangle.NO_BORDER);
            banner.addCell(bannerCell);
            document.add(banner);

            // ---- Customer + rental period ----
            addSectionTitle(document, "Booking For");

            PdfPTable twoCol = new PdfPTable(2);
            twoCol.setWidthPercentage(100);

            PdfPCell customerCell = new PdfPCell();
            customerCell.setBorder(Rectangle.NO_BORDER);
            customerCell.addElement(new Paragraph(text(booking.getCustomerName()),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
            customerCell.addElement(new Paragraph(text(booking.getEmail()), cell));
            if (booking.getPhone() != null) {
                customerCell.addElement(new Paragraph(booking.getPhone(), cell));
            }

            StringBuilder address = new StringBuilder();
            appendPart(address, booking.getAddressLine1(), ", ");
            appendPart(address, booking.getAddressLine2(), ", ");
            appendPart(address, booking.getCityName(), ", ");
            StringBuilder region = new StringBuilder();
            appendPart(region, booking.getStateName(), " ");
            appendPart(region, booking.getPincode(), " ");
            appendPart(address, region.toString(), ", ");
            if (address.length() > 0) {
                customerCell.addElement(new Paragraph(address.toString(), cell));
            }
            if (booking.getDrivingLicenseNo() != null) {
                customerCell.addElement(new Paragraph(
                        "Driving Licence: " + booking.getDrivingLicenseNo(), smallMuted));
            }
            twoCol.addCell(customerCell);

            PdfPTable period = new PdfPTable(2);
            period.setWidthPercentage(100);
            addRow(period, "Pick-up hub", text(booking.getPickupHubName()), label, value);
            addRow(period, "Drop-off hub", text(booking.getDropoffHubName()), label, value);
            addRow(period, "Start date", date(booking.getStartDate()), label, value);
            addRow(period, "End date", date(booking.getEndDate()), label, value);
            addRow(period, "Duration", text(booking.getDuration()) + " day(s)", label, value);

            PdfPCell periodCell = new PdfPCell(period);
            periodCell.setBorder(Rectangle.NO_BORDER);
            twoCol.addCell(periodCell);
            document.add(twoCol);

            // ---- Vehicle ----
            // The category is what the customer reserved. The exact vehicle is
            // allocated at the desk, so there is no registration number yet.
            addSectionTitle(document, "Vehicle Reserved");

            PdfPTable vehicle = new PdfPTable(4);
            vehicle.setWidthPercentage(100);
            addRow(vehicle, "Category", text(booking.getCarTypeName()), label, value);
            addRow(vehicle, "Class", text(booking.getCarClass()), label, value);
            addRow(vehicle, "Daily rate", money(booking.getDailyRate()), label, value);
            addRow(vehicle, "Allocated vehicle",
                    booking.getAssignedCarRegistrationNo() != null
                            ? booking.getAssignedCarRegistrationNo()
                            : "At pick-up",
                    label, value);
            document.add(vehicle);

            // ---- Charges ----
            addSectionTitle(document, "Charges");

            PdfPTable charges = new PdfPTable(new float[] { 6f, 2f, 2f });
            charges.setWidthPercentage(100);
            charges.setSpacingBefore(4f);
            addChargeHeader(charges);

            addChargeRow(charges, "Vehicle rental (" + text(booking.getCarTypeName()) + ")",
                    text(booking.getDuration()) + " day(s) @ " + money(booking.getDailyRate()),
                    money(booking.getVehicleAmount()), cell, false);

            if (booking.getAddons() != null) {
                for (BookingAddonResponse addon : booking.getAddons()) {
                    addChargeRow(charges, "Add-on: " + addon.getAddonName(), "",
                            money(addon.getAddonPrice()), cell, false);
                }
            }

            addChargeRow(charges, "Add-ons subtotal", "",
                    money(booking.getAddonAmount()), cell, false);
            addChargeRow(charges, "GST (18%)", "",
                    money(booking.getTaxAmount()), cell, false);
            addChargeRow(charges, "GRAND TOTAL", "",
                    money(booking.getGrandTotal()), cell, true);

            document.add(charges);

            // ---- What happens next ----
            addSectionTitle(document, "What Happens Next");
            String[] steps = {
                "1. Arrive at the pick-up hub on your start date with your driving licence and photo ID.",
                "2. Our staff will allocate your vehicle and record the fuel level with you.",
                "3. Return the vehicle to the drop-off hub on or before the end date.",
                "4. Your final invoice is issued and e-mailed as soon as the vehicle is returned.",
            };
            for (String step : steps) {
                Paragraph t = new Paragraph(step,
                        FontFactory.getFont(FontFactory.HELVETICA, 8, MUTED));
                t.setSpacingAfter(3f);
                t.setIndentationLeft(4f);
                document.add(t);
            }

            addBrandFooter(document,
                    "This is a computer generated booking confirmation and does not require a signature.");

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new BusinessException(
                    "Failed to generate the booking PDF: " + e.getMessage());
        }
    }

    // =====================================================
    //  INVOICE PDF
    // =====================================================

    private static final String COMPANY_NAME = "FLEMAN Fleet Management Pvt. Ltd.";
    private static final String COMPANY_ADDRESS =
            "Bandra Kurla Complex, Bandra East, Mumbai 400051, Maharashtra, India";
    private static final String COMPANY_CONTACT =
            "Phone: +91 1800 266 3526   |   Email: support@fleman.in   |   GSTIN: 27AAACF1234F1Z5";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter DATE_TIME_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    /** Rupee amount, e.g. 18,758.46 */
    private String money(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }
        return "Rs. " + String.format("%,.2f", value);
    }

    private String text(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }

    private String date(LocalDate value) {
        return value == null ? "-" : value.format(DATE_FMT);
    }

    private String dateTime(LocalDateTime value) {
        return value == null ? "-" : value.format(DATE_TIME_FMT);
    }

    /** A left label + right value row used in the detail blocks. */
    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell l = new PdfPCell(new Phrase(label, labelFont));
        l.setBorder(Rectangle.NO_BORDER);
        l.setPadding(4f);

        PdfPCell v = new PdfPCell(new Phrase(value, valueFont));
        v.setBorder(Rectangle.NO_BORDER);
        v.setPadding(4f);

        table.addCell(l);
        table.addCell(v);
    }

    /** Section heading with a coloured underline. */
    private void addSectionTitle(Document document, String title) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BRAND);
        Paragraph p = new Paragraph(title.toUpperCase(), font);
        p.setSpacingBefore(14f);
        p.setSpacingAfter(6f);
        document.add(p);
    }

    private static final Color BRAND = new Color(37, 99, 235);      // blue-600
    private static final Color LIGHT = new Color(241, 245, 249);    // slate-100
    private static final Color MUTED = new Color(100, 116, 139);    // slate-500
    private static final Color PAID_GREEN = new Color(22, 101, 52);  // green-800
    private static final Color PAID_BG = new Color(220, 252, 231);   // green-100

    @Override
    public byte[] generateInvoicePdf(InvoiceResponse invoice) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font h1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BRAND);
            Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font smallMuted = FontFactory.getFont(FontFactory.HELVETICA, 8, MUTED);
            Font label = FontFactory.getFont(FontFactory.HELVETICA, 9, MUTED);
            Font value = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font tableHead = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font cell = FontFactory.getFont(FontFactory.HELVETICA, 9);

            addBrandHeader(document, "TAX INVOICE",
                    text(invoice.getInvoiceNo()) + "\n"
                            + "Date: " + dateTime(invoice.getInvoiceDate()) + "\n"
                            + "Booking Ref: #" + text(invoice.getBookingId()));

            // -------------------------------------------------
            // Billed to  +  Rental period
            // -------------------------------------------------
            addSectionTitle(document, "Billed To");

            PdfPTable twoCol = new PdfPTable(2);
            twoCol.setWidthPercentage(100);

            // Structured address, assembled the same way the UI does:
            // line 1, line 2, city, then "State 400051".
            StringBuilder address = new StringBuilder();
            appendPart(address, invoice.getAddressLine1(), ", ");
            appendPart(address, invoice.getAddressLine2(), ", ");
            appendPart(address, invoice.getCityName(), ", ");

            StringBuilder region = new StringBuilder();
            appendPart(region, invoice.getStateName(), " ");
            appendPart(region, invoice.getPincode(), " ");
            appendPart(address, region.toString(), ", ");

            PdfPCell customerCell = new PdfPCell();
            customerCell.setBorder(Rectangle.NO_BORDER);
            customerCell.addElement(new Paragraph(text(invoice.getCustomerName()),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
            customerCell.addElement(new Paragraph(text(invoice.getEmail()), cell));
            customerCell.addElement(new Paragraph(text(invoice.getPhone()), cell));
            if (address.length() > 0) {
                customerCell.addElement(new Paragraph(address.toString(), cell));
            }
            customerCell.addElement(new Paragraph(
                    "Driving Licence: " + text(invoice.getDrivingLicenseNo()), smallMuted));
            if (invoice.getPassportNo() != null && !invoice.getPassportNo().isBlank()) {
                customerCell.addElement(new Paragraph(
                        "Passport: " + invoice.getPassportNo(), smallMuted));
            }
            twoCol.addCell(customerCell);

            PdfPTable period = new PdfPTable(2);
            period.setWidthPercentage(100);
            addRow(period, "Pick-up hub", text(invoice.getPickupHubName()), label, value);
            addRow(period, "Drop-off hub", text(invoice.getDropoffHubName()), label, value);
            addRow(period, "Handed over", dateTime(invoice.getHandoverDate()), label, value);
            addRow(period, "Returned", dateTime(invoice.getReturnDate()), label, value);
            addRow(period, "Rental period",
                    date(invoice.getStartDate()) + "  to  " + date(invoice.getEndDate()), label, value);
            addRow(period, "Duration", text(invoice.getDuration()) + " day(s)", label, value);

            PdfPCell periodCell = new PdfPCell(period);
            periodCell.setBorder(Rectangle.NO_BORDER);
            twoCol.addCell(periodCell);

            document.add(twoCol);

            // -------------------------------------------------
            // Vehicle
            // -------------------------------------------------
            addSectionTitle(document, "Vehicle");

            PdfPTable vehicle = new PdfPTable(4);
            vehicle.setWidthPercentage(100);
            addRow(vehicle, "Category", text(invoice.getCarTypeName()), label, value);
            addRow(vehicle, "Registration", text(invoice.getRegistrationNo()), label, value);
            addRow(vehicle, "Vehicle",
                    text(invoice.getBrandName()) + " " + text(invoice.getModelName()), label, value);
            addRow(vehicle, "Fuel out / in",
                    text(invoice.getFuelLevelOut()) + "%  /  " + text(invoice.getFuelLevelIn()) + "%",
                    label, value);
            document.add(vehicle);

            // -------------------------------------------------
            // Charges
            // -------------------------------------------------
            addSectionTitle(document, "Charges");

            PdfPTable charges = new PdfPTable(new float[] { 6f, 2f, 2f });
            charges.setWidthPercentage(100);
            charges.setSpacingBefore(4f);

            addChargeHeader(charges);

            addChargeRow(charges, "Vehicle rental (" + text(invoice.getCarTypeName()) + ")",
                    text(invoice.getDuration()) + " day(s) @ " + money(invoice.getDailyRate()),
                    money(invoice.getVehicleAmount()), cell, false);

            if (invoice.getAddons() != null) {
                for (InvoiceAddonResponse addon : invoice.getAddons()) {
                    addChargeRow(charges, "Add-on: " + addon.getAddonName(), "",
                            money(addon.getAddonPrice()), cell, false);
                }
            }

            addChargeRow(charges, "Add-ons subtotal", "",
                    money(invoice.getAddonAmount()), cell, false);
            addChargeRow(charges, "GST (18%)", "",
                    money(invoice.getTaxAmount()), cell, false);
            addChargeRow(charges, "Booking total", "",
                    money(invoice.getGrandTotal()), cell, false);
            addChargeRow(charges, "Fuel charges on return", "",
                    money(invoice.getFuelCharges()), cell, false);
            addChargeRow(charges, "TOTAL PAYABLE", "",
                    money(invoice.getFinalAmount()), cell, true);

            document.add(charges);

            // -------------------------------------------------
            // Payment status banner
            // -------------------------------------------------
            PdfPTable status = new PdfPTable(new float[] { 6f, 4f });
            status.setWidthPercentage(100);
            status.setSpacingBefore(14f);

            PdfPCell paidCell = new PdfPCell(new Phrase("PAYMENT STATUS:  PAID IN FULL",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, PAID_GREEN)));
            paidCell.setBackgroundColor(PAID_BG);
            paidCell.setPadding(10f);
            paidCell.setBorder(Rectangle.NO_BORDER);
            status.addCell(paidCell);

            PdfPCell settledCell = new PdfPCell(new Phrase(
                    "Settled on " + dateTime(invoice.getReturnDate()), smallMuted));
            settledCell.setBackgroundColor(PAID_BG);
            settledCell.setPadding(10f);
            settledCell.setBorder(Rectangle.NO_BORDER);
            settledCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            status.addCell(settledCell);

            document.add(status);

            // -------------------------------------------------
            // Terms & Conditions
            // -------------------------------------------------
            addSectionTitle(document, "Terms & Conditions");

            String[] terms = {
                "1. Fuel is charged like-for-like. A shortfall against the level recorded at "
                    + "hand-over is billed as fuel charges.",
                "2. Traffic fines, tolls and parking penalties incurred during the rental remain "
                    + "the responsibility of the hirer.",
                "3. Damage not covered by the selected insurance add-on is chargeable at assessment.",
                "4. Any dispute regarding this invoice must be raised within 7 days of the invoice date.",
            };

            for (String term : terms) {
                Paragraph t = new Paragraph(term,
                        FontFactory.getFont(FontFactory.HELVETICA, 8, MUTED));
                t.setSpacingAfter(3f);
                t.setIndentationLeft(4f);
                document.add(t);
            }

            addBrandFooter(document,
                    "This is a computer generated invoice and does not require a signature.");

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new BusinessException(
                    "Failed to generate the invoice PDF: " + e.getMessage());
        }
    }

    /** One line in the charges table. The last row is highlighted. */
    private void addChargeRow(PdfPTable table, String description, String details,
                              String amount, Font font, boolean total) {

        Font f = total ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10) : font;

        PdfPCell d = new PdfPCell(new Phrase(description, f));
        PdfPCell m = new PdfPCell(new Phrase(details, f));
        PdfPCell a = new PdfPCell(new Phrase(amount, f));

        for (PdfPCell c : new PdfPCell[] { d, m, a }) {
            c.setPadding(6f);
            c.setBorder(Rectangle.BOTTOM);
            c.setBorderColor(LIGHT);
            if (total) {
                c.setBackgroundColor(LIGHT);
            }
        }
        m.setHorizontalAlignment(Element.ALIGN_RIGHT);
        a.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(d);
        table.addCell(m);
        table.addCell(a);
    }

    /** Appends a value with a separator, skipping blanks so no stray commas. */
    private void appendPart(StringBuilder target, String value, String separator) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (target.length() > 0) {
            target.append(separator);
        }
        target.append(value.trim());
    }


    // =====================================================
    //  SHARED BRANDING
    //  Used by BOTH the booking confirmation and the invoice, so the two
    //  documents can never drift apart visually.
    // =====================================================

    /** Company block on the left, document title and reference on the right. */
    private void addBrandHeader(Document document, String title, String rightBlock)
            throws DocumentException {

        Font h1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BRAND);
        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font smallMuted = FontFactory.getFont(FontFactory.HELVETICA, 8, MUTED);

        PdfPTable header = new PdfPTable(new float[] { 6f, 4f });
        header.setWidthPercentage(100);

        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(Rectangle.NO_BORDER);
        companyCell.addElement(new Paragraph(COMPANY_NAME, companyFont));
        companyCell.addElement(new Paragraph(COMPANY_ADDRESS, smallMuted));
        companyCell.addElement(new Paragraph(COMPANY_CONTACT, smallMuted));
        header.addCell(companyCell);

        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph docTitle = new Paragraph(title, h1);
        docTitle.setAlignment(Element.ALIGN_RIGHT);
        titleCell.addElement(docTitle);

        Paragraph ref = new Paragraph(rightBlock, smallMuted);
        ref.setAlignment(Element.ALIGN_RIGHT);
        titleCell.addElement(ref);
        header.addCell(titleCell);

        document.add(header);

        Paragraph spacer = new Paragraph(" ");
        spacer.setSpacingBefore(4f);
        document.add(spacer);

        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BRAND);
        separator.setLineWidth(2f);
        document.add(separator);
    }

    /** Divider, company details and the closing note. */
    private void addBrandFooter(Document document, String note) throws DocumentException {

        Font smallMuted = FontFactory.getFont(FontFactory.HELVETICA, 8, MUTED);

        Paragraph spacer = new Paragraph(" ");
        spacer.setSpacingBefore(14f);
        document.add(spacer);

        LineSeparator footerRule = new LineSeparator();
        footerRule.setLineColor(LIGHT);
        footerRule.setLineWidth(1f);
        document.add(footerRule);

        Paragraph footer = new Paragraph(
                COMPANY_NAME + "\n"
                        + COMPANY_ADDRESS + "\n"
                        + COMPANY_CONTACT + "\n\n"
                        + note + "\n"
                        + "Thank you for choosing " + COMPANY_NAME + ".",
                smallMuted);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(12f);
        document.add(footer);
    }

    /** The blue heading row of a charges table. */
    private void addChargeHeader(PdfPTable table) {

        Font tableHead = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);

        for (String head : new String[] { "Description", "Details", "Amount" }) {
            PdfPCell c = new PdfPCell(new Phrase(head, tableHead));
            c.setBackgroundColor(BRAND);
            c.setPadding(6f);
            c.setBorder(Rectangle.NO_BORDER);
            if (!"Description".equals(head)) {
                c.setHorizontalAlignment(Element.ALIGN_RIGHT);
            }
            table.addCell(c);
        }
    }

}

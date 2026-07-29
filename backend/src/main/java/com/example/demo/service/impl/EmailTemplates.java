package com.example.demo.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.demo.dto.response.BookingAddonResponse;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.dto.response.InvoiceAddonResponse;
import com.example.demo.dto.response.InvoiceResponse;

/**
 * HTML e-mail bodies.
 *
 * WHY THE MARKUP LOOKS OLD-FASHIONED
 * ----------------------------------
 * E-mail clients are not browsers. Outlook renders with Word, Gmail strips
 * <style> blocks and most clients ignore flexbox and grid. So these templates
 * deliberately use:
 *
 *   - tables for layout, not divs
 *   - inline styles on every element, not CSS classes
 *   - a 600px wide container, the safe maximum
 *   - width="100%" tables so it still reads on a phone
 *   - web-safe fonts only
 *
 * That is the standard way to build HTML email and it renders correctly in
 * Gmail, Outlook, Apple Mail and the mobile clients.
 */
public final class EmailTemplates {

    private EmailTemplates() {
    }

    // Brand palette, kept in step with the invoice PDF.
    private static final String BRAND = "#2563EB";
    private static final String DARK = "#0F172A";
    private static final String MUTED = "#64748B";
    private static final String LINE = "#E2E8F0";
    private static final String LIGHT_BG = "#F1F5F9";

    public static final String COMPANY = "FLEMAN Fleet Management Pvt. Ltd.";
    public static final String ADDRESS =
            "Bandra Kurla Complex, Bandra East, Mumbai 400051, Maharashtra, India";
    public static final String SUPPORT_PHONE = "+91 1800 266 3526";
    public static final String SUPPORT_EMAIL = "support@fleman.in";
    public static final String GSTIN = "27AAACF1234F1Z5";

    private static final DateTimeFormatter D = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private static String money(BigDecimal v) {
        return "&#8377;" + String.format("%,.2f", v == null ? BigDecimal.ZERO : v);
    }

    private static String txt(Object v) {
        return v == null ? "-" : String.valueOf(v);
    }

    private static String date(LocalDate v) {
        return v == null ? "-" : v.format(D);
    }

    private static String dateTime(LocalDateTime v) {
        return v == null ? "-" : v.format(DT);
    }

    /** A label/value row inside one of the detail cards. */
    private static String row(String label, String value) {
        return """
            <tr>
              <td style="padding:6px 0;font-size:13px;color:%s;">%s</td>
              <td style="padding:6px 0;font-size:13px;color:%s;font-weight:bold;text-align:right;">%s</td>
            </tr>
            """.formatted(MUTED, label, DARK, value);
    }

    /** A money line in the charges table. */
    private static String charge(String label, String value, boolean total) {
        String weight = total ? "bold" : "normal";
        String size = total ? "16px" : "13px";
        String colour = total ? BRAND : DARK;
        String border = total ? "border-top:2px solid " + LINE + ";" : "";
        return """
            <tr>
              <td style="padding:8px 0;%sfont-size:%s;color:%s;font-weight:%s;">%s</td>
              <td style="padding:8px 0;%sfont-size:%s;color:%s;font-weight:bold;text-align:right;">%s</td>
            </tr>
            """.formatted(border, size, MUTED, weight, label,
                          border, size, colour, value);
    }

    private static String card(String title, String innerRows) {
        return """
            <table width="100%%" cellpadding="0" cellspacing="0" role="presentation"
                   style="background:%s;border-radius:10px;padding:16px;margin-bottom:16px;">
              <tr><td>
                <p style="margin:0 0 10px;font-size:11px;letter-spacing:.08em;
                          text-transform:uppercase;color:%s;font-weight:bold;">%s</p>
                <table width="100%%" cellpadding="0" cellspacing="0" role="presentation">%s</table>
              </td></tr>
            </table>
            """.formatted(LIGHT_BG, BRAND, title, innerRows);
    }

    /**
     * The e-mail sent once a vehicle has been returned, with the invoice PDF
     * attached.
     */
    public static String invoiceEmail(InvoiceResponse inv) {

        StringBuilder addonRows = new StringBuilder();
        if (inv.getAddons() != null) {
            for (InvoiceAddonResponse a : inv.getAddons()) {
                addonRows.append(charge("&nbsp;&nbsp;Add-on: " + a.getAddonName(),
                        money(a.getAddonPrice()), false));
            }
        }

        String bookingCard = card("Booking details",
                row("Booking reference", "#" + txt(inv.getBookingId()))
              + row("Invoice number", txt(inv.getInvoiceNo()))
              + row("Rental period", date(inv.getStartDate()) + " &rarr; " + date(inv.getEndDate()))
              + row("Duration", txt(inv.getDuration()) + " day(s)")
              + row("Pick-up hub", txt(inv.getPickupHubName()))
              + row("Drop-off hub", txt(inv.getDropoffHubName())));

        String vehicleCard = card("Vehicle",
                row("Category", txt(inv.getCarTypeName()))
              + row("Vehicle", txt(inv.getBrandName()) + " " + txt(inv.getModelName()))
              + row("Registration", txt(inv.getRegistrationNo())));

        String returnCard = card("Return summary",
                row("Handed over", dateTime(inv.getHandoverDate()))
              + row("Returned", dateTime(inv.getReturnDate()))
              + row("Fuel out / in",
                    txt(inv.getFuelLevelOut()) + "% / " + txt(inv.getFuelLevelIn()) + "%"));

        String charges =
                charge("Vehicle rental (" + txt(inv.getDuration()) + " day(s))",
                       money(inv.getVehicleAmount()), false)
              + addonRows
              + charge("Add-ons subtotal", money(inv.getAddonAmount()), false)
              + charge("GST (18%)", money(inv.getTaxAmount()), false)
              + charge("Fuel charges", money(inv.getFuelCharges()), false)
              + charge("Total paid", money(inv.getFinalAmount()), true);

        return """
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Your FLEMAN invoice</title>
</head>
<body style="margin:0;padding:0;background:#F8FAFC;
             font-family:Arial,Helvetica,sans-serif;-webkit-font-smoothing:antialiased;">

  <!-- Preview text shown in the inbox list, hidden in the body -->
  <div style="display:none;max-height:0;overflow:hidden;">
    Invoice %s - thank you for renting with FLEMAN. Total paid %s.
  </div>

  <table width="100%%" cellpadding="0" cellspacing="0" role="presentation"
         style="background:#F8FAFC;padding:24px 12px;">
    <tr>
      <td align="center">

        <table width="600" cellpadding="0" cellspacing="0" role="presentation"
               style="max-width:600px;width:100%%;background:#FFFFFF;border-radius:14px;
                      overflow:hidden;box-shadow:0 1px 3px rgba(15,23,42,.08);">

          <!-- Header -->
          <tr>
            <td style="background:%s;padding:28px 32px;">
              <table width="100%%" cellpadding="0" cellspacing="0" role="presentation">
                <tr>
                  <td>
                    <p style="margin:0;font-size:26px;font-weight:bold;color:#FFFFFF;
                              letter-spacing:-.02em;">FLEMAN</p>
                    <p style="margin:4px 0 0;font-size:12px;color:#BFDBFE;">
                      Fleet Management &amp; Car Rental
                    </p>
                  </td>
                  <td align="right">
                    <p style="margin:0;font-size:11px;color:#BFDBFE;text-transform:uppercase;
                              letter-spacing:.08em;">Invoice</p>
                    <p style="margin:4px 0 0;font-size:16px;font-weight:bold;color:#FFFFFF;">%s</p>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- Greeting -->
          <tr>
            <td style="padding:32px 32px 8px;">
              <h1 style="margin:0 0 8px;font-size:20px;color:%s;">Thank you, %s</h1>
              <p style="margin:0;font-size:14px;line-height:22px;color:%s;">
                Your vehicle has been returned and your rental is now complete.
                The full invoice is attached to this e-mail as a PDF.
              </p>
            </td>
          </tr>

          <!-- Amount banner -->
          <tr>
            <td style="padding:20px 32px 4px;">
              <table width="100%%" cellpadding="0" cellspacing="0" role="presentation"
                     style="background:%s;border-radius:10px;">
                <tr>
                  <td style="padding:18px 20px;">
                    <p style="margin:0;font-size:11px;letter-spacing:.08em;
                              text-transform:uppercase;color:#BFDBFE;font-weight:bold;">Total paid</p>
                    <p style="margin:6px 0 0;font-size:28px;font-weight:bold;color:#FFFFFF;">%s</p>
                  </td>
                  <td align="right" style="padding:18px 20px;">
                    <span style="display:inline-block;background:#DCFCE7;color:#166534;
                                 font-size:12px;font-weight:bold;padding:6px 12px;
                                 border-radius:999px;">PAID</span>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- Detail cards -->
          <tr><td style="padding:20px 32px 0;">%s%s%s</td></tr>

          <!-- Charges -->
          <tr>
            <td style="padding:4px 32px 8px;">
              <p style="margin:0 0 8px;font-size:11px;letter-spacing:.08em;
                        text-transform:uppercase;color:%s;font-weight:bold;">Payment summary</p>
              <table width="100%%" cellpadding="0" cellspacing="0" role="presentation">%s</table>
            </td>
          </tr>

          <!-- Invoice note -->
          <tr>
            <td style="padding:16px 32px 28px;">
              <table width="100%%" cellpadding="0" cellspacing="0" role="presentation"
                     style="border:1px solid %s;border-radius:10px;">
                <tr>
                  <td style="padding:16px 20px;font-size:13px;line-height:20px;color:%s;">
                    <strong style="color:%s;">Your invoice is attached</strong><br>
                    Look for <strong>Invoice-%s.pdf</strong> on this e-mail. You can also
                    view and download it any time from the <strong>Invoices</strong> tab
                    of your FLEMAN dashboard.
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- Footer -->
          <tr>
            <td style="background:%s;padding:24px 32px;">
              <p style="margin:0 0 6px;font-size:13px;font-weight:bold;color:%s;">%s</p>
              <p style="margin:0;font-size:12px;line-height:19px;color:%s;">
                %s<br>
                Phone %s &nbsp;|&nbsp; %s<br>
                GSTIN %s
              </p>
              <p style="margin:14px 0 0;font-size:11px;color:%s;">
                This is a computer generated e-mail. Please do not reply directly.
              </p>
            </td>
          </tr>

        </table>

        <p style="margin:16px 0 0;font-size:11px;color:%s;">
          &copy; 2026 %s. All rights reserved.
        </p>

      </td>
    </tr>
  </table>
</body>
</html>
""".formatted(
        txt(inv.getInvoiceNo()), money(inv.getFinalAmount()),   // preview text
        BRAND,                                                   // header bg
        txt(inv.getInvoiceNo()),                                 // header invoice no
        DARK, txt(inv.getCustomerName()), MUTED,                 // greeting
        BRAND, money(inv.getFinalAmount()),                      // amount banner
        bookingCard, vehicleCard, returnCard,                    // cards
        BRAND, charges,                                          // payment summary
        LINE, MUTED, DARK, txt(inv.getInvoiceNo()),              // attachment note
        LIGHT_BG, DARK, COMPANY, MUTED, ADDRESS,                 // footer
        SUPPORT_PHONE, SUPPORT_EMAIL, GSTIN, MUTED,
        MUTED, COMPANY);
    }

    /**
     * Booking confirmation e-mail, sent as soon as a booking is made.
     *
     * Uses exactly the same layout, palette and components as the invoice
     * e-mail, so the two look like they came from the same company.
     */
    public static String bookingEmail(BookingResponse b) {

        StringBuilder addonRows = new StringBuilder();
        if (b.getAddons() != null) {
            for (BookingAddonResponse a : b.getAddons()) {
                addonRows.append(charge("&nbsp;&nbsp;Add-on: " + a.getAddonName(),
                        money(a.getAddonPrice()), false));
            }
        }

        String bookingCard = card("Booking details",
                row("Booking reference", "#" + txt(b.getBookingId()))
              + row("Status", txt(b.getBookingStatus()))
              + row("Rental period", date(b.getStartDate()) + " &rarr; " + date(b.getEndDate()))
              + row("Duration", txt(b.getDuration()) + " day(s)")
              + row("Pick-up hub", txt(b.getPickupHubName()))
              + row("Drop-off hub", txt(b.getDropoffHubName())));

        String vehicleCard = card("Vehicle reserved",
                row("Category", txt(b.getCarTypeName()))
              + row("Class", txt(b.getCarClass()))
              + row("Daily rate", money(b.getDailyRate()))
              + row("Your vehicle", "Allocated at pick-up"));

        String charges =
                charge("Vehicle rental (" + txt(b.getDuration()) + " day(s))",
                       money(b.getVehicleAmount()), false)
              + addonRows
              + charge("Add-ons subtotal", money(b.getAddonAmount()), false)
              + charge("GST (18%)", money(b.getTaxAmount()), false)
              + charge("Grand total", money(b.getGrandTotal()), true);

        return """
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Your FLEMAN booking is confirmed</title>
</head>
<body style="margin:0;padding:0;background:#F8FAFC;
             font-family:Arial,Helvetica,sans-serif;-webkit-font-smoothing:antialiased;">

  <div style="display:none;max-height:0;overflow:hidden;">
    Booking #%s confirmed - %s from %s. Total %s.
  </div>

  <table width="100%%" cellpadding="0" cellspacing="0" role="presentation"
         style="background:#F8FAFC;padding:24px 12px;">
    <tr>
      <td align="center">

        <table width="600" cellpadding="0" cellspacing="0" role="presentation"
               style="max-width:600px;width:100%%;background:#FFFFFF;border-radius:14px;
                      overflow:hidden;box-shadow:0 1px 3px rgba(15,23,42,.08);">

          <!-- Header -->
          <tr>
            <td style="background:%s;padding:28px 32px;">
              <table width="100%%" cellpadding="0" cellspacing="0" role="presentation">
                <tr>
                  <td>
                    <p style="margin:0;font-size:26px;font-weight:bold;color:#FFFFFF;
                              letter-spacing:-.02em;">FLEMAN</p>
                    <p style="margin:4px 0 0;font-size:12px;color:#BFDBFE;">
                      Fleet Management &amp; Car Rental
                    </p>
                  </td>
                  <td align="right">
                    <p style="margin:0;font-size:11px;color:#BFDBFE;text-transform:uppercase;
                              letter-spacing:.08em;">Booking</p>
                    <p style="margin:4px 0 0;font-size:16px;font-weight:bold;color:#FFFFFF;">#%s</p>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- Greeting -->
          <tr>
            <td style="padding:32px 32px 8px;">
              <h1 style="margin:0 0 8px;font-size:20px;color:%s;">You are all set, %s</h1>
              <p style="margin:0;font-size:14px;line-height:22px;color:%s;">
                Your booking is confirmed. A copy is attached as a PDF - please
                bring your driving licence and a photo ID when you collect the vehicle.
              </p>
            </td>
          </tr>

          <!-- Amount banner -->
          <tr>
            <td style="padding:20px 32px 4px;">
              <table width="100%%" cellpadding="0" cellspacing="0" role="presentation"
                     style="background:%s;border-radius:10px;">
                <tr>
                  <td style="padding:18px 20px;">
                    <p style="margin:0;font-size:11px;letter-spacing:.08em;
                              text-transform:uppercase;color:#BFDBFE;font-weight:bold;">Grand total</p>
                    <p style="margin:6px 0 0;font-size:28px;font-weight:bold;color:#FFFFFF;">%s</p>
                  </td>
                  <td align="right" style="padding:18px 20px;">
                    <span style="display:inline-block;background:#FEF3C7;color:#92400E;
                                 font-size:12px;font-weight:bold;padding:6px 12px;
                                 border-radius:999px;">%s</span>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- Detail cards -->
          <tr><td style="padding:20px 32px 0;">%s%s</td></tr>

          <!-- Charges -->
          <tr>
            <td style="padding:4px 32px 8px;">
              <p style="margin:0 0 8px;font-size:11px;letter-spacing:.08em;
                        text-transform:uppercase;color:%s;font-weight:bold;">Price breakdown</p>
              <table width="100%%" cellpadding="0" cellspacing="0" role="presentation">%s</table>
            </td>
          </tr>

          <!-- What happens next -->
          <tr>
            <td style="padding:16px 32px 28px;">
              <table width="100%%" cellpadding="0" cellspacing="0" role="presentation"
                     style="border:1px solid %s;border-radius:10px;">
                <tr>
                  <td style="padding:16px 20px;font-size:13px;line-height:20px;color:%s;">
                    <strong style="color:%s;">What happens next</strong><br>
                    1. Arrive at the pick-up hub on your start date with your licence and photo ID.<br>
                    2. Our staff allocate your vehicle and record the fuel level with you.<br>
                    3. Return it to the drop-off hub on or before the end date.<br>
                    4. Your final invoice is e-mailed as soon as the vehicle is returned.
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- Footer -->
          <tr>
            <td style="background:%s;padding:24px 32px;">
              <p style="margin:0 0 6px;font-size:13px;font-weight:bold;color:%s;">%s</p>
              <p style="margin:0;font-size:12px;line-height:19px;color:%s;">
                %s<br>
                Phone %s &nbsp;|&nbsp; %s<br>
                GSTIN %s
              </p>
              <p style="margin:14px 0 0;font-size:11px;color:%s;">
                This is a computer generated e-mail. Please do not reply directly.
              </p>
            </td>
          </tr>

        </table>

        <p style="margin:16px 0 0;font-size:11px;color:%s;">
          &copy; 2026 %s. All rights reserved.
        </p>

      </td>
    </tr>
  </table>
</body>
</html>
""".formatted(
        txt(b.getBookingId()), txt(b.getCarTypeName()), txt(b.getPickupHubName()),
            money(b.getGrandTotal()),                            // preview text
        BRAND, txt(b.getBookingId()),                            // header
        DARK, txt(b.getCustomerName()), MUTED,                   // greeting
        BRAND, money(b.getGrandTotal()), txt(b.getBookingStatus()),  // amount banner
        bookingCard, vehicleCard,                                // cards
        BRAND, charges,                                          // price breakdown
        LINE, MUTED, DARK,                                       // next steps
        LIGHT_BG, DARK, COMPANY, MUTED, ADDRESS,                 // footer
        SUPPORT_PHONE, SUPPORT_EMAIL, GSTIN, MUTED,
        MUTED, COMPANY);
    }
}

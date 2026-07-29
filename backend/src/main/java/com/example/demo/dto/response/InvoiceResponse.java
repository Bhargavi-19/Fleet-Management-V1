package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.entity.InvoiceHeader;

/**
 * A complete invoice, as shown on the customer's Invoice page and printed
 * onto the PDF.
 *
 * Everything here is a snapshot taken when the vehicle was returned, so a
 * later price change never rewrites an invoice that was already issued.
 */
public class InvoiceResponse {

    // ---------- Invoice ----------
    private Long invoiceId;
    private String invoiceNo;
    private LocalDateTime invoiceDate;
    private Long bookingId;

    // ---------- Customer ----------
    private String customerId;
    private String customerName;
    private String email;
    private String phone;
    private String drivingLicenseNo;
    private String passportNo;
    private String addressLine1;
    private String addressLine2;
    private String cityName;
    private String stateName;
    private String pincode;

    // ---------- Vehicle ----------
    private Integer carId;
    private String registrationNo;
    private String brandName;
    private String modelName;
    private String carTypeName;

    // ---------- Hubs ----------
    private String pickupHubName;
    private String dropoffHubName;

    // ---------- Rental period ----------
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime handoverDate;
    private LocalDateTime returnDate;
    private Integer duration;

    // ---------- Rates ----------
    private BigDecimal dailyRate;
    private BigDecimal weeklyRate;
    private BigDecimal monthlyRate;

    // ---------- Charges ----------
    private BigDecimal vehicleAmount;
    private BigDecimal addonAmount;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;   // booking total agreed up front
    private BigDecimal fuelCharges;  // added at return
    private BigDecimal finalAmount;  // grandTotal + fuelCharges

    // ---------- Fuel ----------
    private Integer fuelLevelOut;
    private Integer fuelLevelIn;

    // ---------- Add-ons ----------
    private List<InvoiceAddonResponse> addons = new ArrayList<>();

    public InvoiceResponse() {
    }

    /** Copies everything off the stored invoice. Add-ons are set separately. */
    public static InvoiceResponse fromEntity(InvoiceHeader invoice) {

        InvoiceResponse r = new InvoiceResponse();

        r.setInvoiceId(invoice.getInvoiceId());
        r.setInvoiceNo(invoice.getInvoiceNo());
        r.setInvoiceDate(invoice.getInvoiceDate());
        r.setBookingId(invoice.getBookingId());

        r.setCustomerId(invoice.getCustomerId());

        String name = invoice.getFirstName() == null ? "" : invoice.getFirstName();
        if (invoice.getLastName() != null && !invoice.getLastName().isBlank()) {
            name = name + " " + invoice.getLastName();
        }
        r.setCustomerName(name.trim());

        r.setEmail(invoice.getEmail());
        r.setPhone(invoice.getPhone());
        r.setDrivingLicenseNo(invoice.getDrivingLicenseNo());
        r.setPassportNo(invoice.getPassportNo());
        r.setAddressLine1(invoice.getAddressLine1());
        r.setAddressLine2(invoice.getAddressLine2());
        r.setPincode(invoice.getPincode());

        r.setCarId(invoice.getCarId());
        r.setRegistrationNo(invoice.getRegistrationNo());
        r.setBrandName(invoice.getBrandName());
        r.setModelName(invoice.getModelName());

        r.setPickupHubName(invoice.getPickupHubName());
        r.setDropoffHubName(invoice.getDropoffHubName());

        r.setStartDate(invoice.getStartDate());
        r.setEndDate(invoice.getEndDate());
        r.setHandoverDate(invoice.getHandoverDate());
        r.setReturnDate(invoice.getReturnDate());
        r.setDuration(invoice.getDuration());

        r.setDailyRate(invoice.getDailyRate());
        r.setWeeklyRate(invoice.getWeeklyRate());
        r.setMonthlyRate(invoice.getMonthlyRate());

        r.setVehicleAmount(invoice.getVehicleAmount());
        r.setAddonAmount(invoice.getAddonAmount());
        r.setTaxAmount(invoice.getTaxAmount());
        r.setGrandTotal(invoice.getGrandTotal());
        r.setFuelCharges(invoice.getFuelCharges());

        // What the customer actually pays once fuel is settled.
        BigDecimal total = invoice.getGrandTotal() == null
                ? BigDecimal.ZERO : invoice.getGrandTotal();
        BigDecimal fuel = invoice.getFuelCharges() == null
                ? BigDecimal.ZERO : invoice.getFuelCharges();
        r.setFinalAmount(total.add(fuel));

        r.setFuelLevelOut(invoice.getFuelLevelOut());
        r.setFuelLevelIn(invoice.getFuelLevelIn());

        return r;
    }

    // ---------- getters / setters ----------

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }

    public LocalDateTime getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDateTime invoiceDate) { this.invoiceDate = invoiceDate; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDrivingLicenseNo() { return drivingLicenseNo; }
    public void setDrivingLicenseNo(String drivingLicenseNo) { this.drivingLicenseNo = drivingLicenseNo; }

    public String getPassportNo() { return passportNo; }
    public void setPassportNo(String passportNo) { this.passportNo = passportNo; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public Integer getCarId() { return carId; }
    public void setCarId(Integer carId) { this.carId = carId; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getCarTypeName() { return carTypeName; }
    public void setCarTypeName(String carTypeName) { this.carTypeName = carTypeName; }

    public String getPickupHubName() { return pickupHubName; }
    public void setPickupHubName(String pickupHubName) { this.pickupHubName = pickupHubName; }

    public String getDropoffHubName() { return dropoffHubName; }
    public void setDropoffHubName(String dropoffHubName) { this.dropoffHubName = dropoffHubName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDateTime getHandoverDate() { return handoverDate; }
    public void setHandoverDate(LocalDateTime handoverDate) { this.handoverDate = handoverDate; }

    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public BigDecimal getDailyRate() { return dailyRate; }
    public void setDailyRate(BigDecimal dailyRate) { this.dailyRate = dailyRate; }

    public BigDecimal getWeeklyRate() { return weeklyRate; }
    public void setWeeklyRate(BigDecimal weeklyRate) { this.weeklyRate = weeklyRate; }

    public BigDecimal getMonthlyRate() { return monthlyRate; }
    public void setMonthlyRate(BigDecimal monthlyRate) { this.monthlyRate = monthlyRate; }

    public BigDecimal getVehicleAmount() { return vehicleAmount; }
    public void setVehicleAmount(BigDecimal vehicleAmount) { this.vehicleAmount = vehicleAmount; }

    public BigDecimal getAddonAmount() { return addonAmount; }
    public void setAddonAmount(BigDecimal addonAmount) { this.addonAmount = addonAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

    public BigDecimal getFuelCharges() { return fuelCharges; }
    public void setFuelCharges(BigDecimal fuelCharges) { this.fuelCharges = fuelCharges; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public Integer getFuelLevelOut() { return fuelLevelOut; }
    public void setFuelLevelOut(Integer fuelLevelOut) { this.fuelLevelOut = fuelLevelOut; }

    public Integer getFuelLevelIn() { return fuelLevelIn; }
    public void setFuelLevelIn(Integer fuelLevelIn) { this.fuelLevelIn = fuelLevelIn; }

    public List<InvoiceAddonResponse> getAddons() { return addons; }
    public void setAddons(List<InvoiceAddonResponse> addons) { this.addons = addons; }
}

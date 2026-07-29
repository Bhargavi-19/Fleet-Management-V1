package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.demo.entity.base.BaseEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "invoice_header")
public class InvoiceHeader extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    // ==========================================
    // Booking Reference
    // ==========================================

    @Column(name = "booking_id", nullable = false, unique = true)
    private Long bookingId;

    // ==========================================
<<<<<<< HEAD
=======
    // Invoice identity (BRD: Invoice Header Table)
    // ==========================================

    /** Human readable number shown on the PDF, e.g. INV-2026-000042. */
    @Column(name = "invoice_no", unique = true, length = 30)
    private String invoiceNo;

    /** When the invoice was raised - i.e. when the car came back. */
    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    // ==========================================
>>>>>>> Developer
    // Customer Snapshot
    // ==========================================

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "driving_license_no")
    private String drivingLicenseNo;

    @Column(name = "passport_no")
    private String passportNo;

<<<<<<< HEAD
=======
    // Address snapshot, copied from the booking (BRD: Invoice Header Table)
    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "city_id")
    private Integer cityId;

    @Column(name = "state_id")
    private Integer stateId;

    @Column(name = "pincode", length = 10)
    private String pincode;

>>>>>>> Developer
    // ==========================================
    // Vehicle Snapshot
    // ==========================================

    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "registration_no")
    private String registrationNo;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "car_type_id")
    private Long carTypeId;

    // ==========================================
    // Hub Snapshot
    // ==========================================

    @Column(name = "pickup_hub_id")
    private Integer pickupHubId;

    @Column(name = "pickup_hub_name")
    private String pickupHubName;

    @Column(name = "dropoff_hub_id")
    private Integer dropoffHubId;

    @Column(name = "dropoff_hub_name")
    private String dropoffHubName;

    // ==========================================
    // Booking Dates
    // ==========================================

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "handover_date")
    private LocalDateTime handoverDate;

    // ==========================================
    // Charges
    // ==========================================

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "daily_rate", precision = 12, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "weekly_rate", precision = 12, scale = 2)
    private BigDecimal weeklyRate;

    @Column(name = "monthly_rate", precision = 12, scale = 2)
    private BigDecimal monthlyRate;

    @Column(name = "vehicle_amount", precision = 12, scale = 2)
    private BigDecimal vehicleAmount;

    @Column(name = "addon_amount", precision = 12, scale = 2)
    private BigDecimal addonAmount;

    @Column(name = "tax_amount", precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "grand_total", precision = 12, scale = 2)
    private BigDecimal grandTotal;

    // ==========================================
    // Fuel
    // ==========================================

    @Column(name = "fuel_level_out")
    private Integer fuelLevelOut;

    
 // ==========================================
 // Return Details
 // ==========================================

 @Column(name = "fuel_level_in")
 private Integer fuelLevelIn;

 @Column(name = "fuel_charges", precision = 12, scale = 2)
 private BigDecimal fuelCharges;
    public InvoiceHeader() {
    }

    // ---------- Invoice ----------

    public Long getInvoiceId() {
        return invoiceId;
    }
    

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    // ---------- Customer ----------

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDrivingLicenseNo() {
        return drivingLicenseNo;
    }

    public void setDrivingLicenseNo(String drivingLicenseNo) {
        this.drivingLicenseNo = drivingLicenseNo;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    // ---------- Vehicle ----------

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Long getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(Long carTypeId) {
        this.carTypeId = carTypeId;
    }

    // ---------- Hub ----------

    public Integer getPickupHubId() {
        return pickupHubId;
    }

    public void setPickupHubId(Integer pickupHubId) {
        this.pickupHubId = pickupHubId;
    }

    public String getPickupHubName() {
        return pickupHubName;
    }

    public void setPickupHubName(String pickupHubName) {
        this.pickupHubName = pickupHubName;
    }

    public Integer getDropoffHubId() {
        return dropoffHubId;
    }

    public void setDropoffHubId(Integer dropoffHubId) {
        this.dropoffHubId = dropoffHubId;
    }

    public String getDropoffHubName() {
        return dropoffHubName;
    }

    public void setDropoffHubName(String dropoffHubName) {
        this.dropoffHubName = dropoffHubName;
    }

    // ---------- Dates ----------

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getHandoverDate() {
        return handoverDate;
    }

    public void setHandoverDate(LocalDateTime handoverDate) {
        this.handoverDate = handoverDate;
    }

    // ---------- Charges ----------

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public BigDecimal getWeeklyRate() {
        return weeklyRate;
    }

    public void setWeeklyRate(BigDecimal weeklyRate) {
        this.weeklyRate = weeklyRate;
    }

    public BigDecimal getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(BigDecimal monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public BigDecimal getVehicleAmount() {
        return vehicleAmount;
    }

    public void setVehicleAmount(BigDecimal vehicleAmount) {
        this.vehicleAmount = vehicleAmount;
    }

    public BigDecimal getAddonAmount() {
        return addonAmount;
    }

    public void setAddonAmount(BigDecimal addonAmount) {
        this.addonAmount = addonAmount;
    }
    

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    // ---------- Fuel ----------

    public Integer getFuelLevelOut() {
        return fuelLevelOut;
    }

    public void setFuelLevelOut(Integer fuelLevelOut) {
        this.fuelLevelOut = fuelLevelOut;
    }

	public Integer getFuelLevelIn() {
		return fuelLevelIn;
	}

	public void setFuelLevelIn(Integer fuelLevelIn) {
		this.fuelLevelIn = fuelLevelIn;
	}

	public BigDecimal getFuelCharges() {
		return fuelCharges;
	}

	public void setFuelCharges(BigDecimal fuelCharges) {
		this.fuelCharges = fuelCharges;
	}
    
    
    
<<<<<<< HEAD
=======

    // ---------- Invoice identity ----------

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    // ---------- Address snapshot ----------

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
>>>>>>> Developer
}

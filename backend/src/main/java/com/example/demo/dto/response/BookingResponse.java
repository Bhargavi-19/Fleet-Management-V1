package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.enums.BookingStatus;

/**
 * Single booking, as returned to both the customer and staff screens.
 *
 * The staff hand-over and return screens need the assigned vehicle,
 * the fuel readings and the add-on lines, so those are included here
 * instead of being fetched through extra API calls.
 */
public class BookingResponse {

    // ---------- Booking ----------
    private Long bookingId;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingDate;

    // ---------- Customer ----------
    private String customerId;
    private String customerName;
    private String email;
    private String phone;
    private String drivingLicenseNo;

    // ---------- Address (structured, never a single blob) ----------
    private String addressLine1;
    private String addressLine2;
    private Integer cityId;
    private String cityName;
    private Integer stateId;
    private String stateName;
    private String pincode;

    // ---------- Vehicle ----------
    // carId stays null until staff assign a vehicle at hand-over.
    private Long carId;
    private Long carTypeId;

    /** e.g. "Full Size SUV" - what the customer actually chose. */
    private String carTypeName;
    private String carClass;
    private String carTypeImageUrl;

    // ---------- Dates ----------
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer duration;

    // ---------- Hubs ----------
    private Integer pickupHubId;
    private String pickupHubName;
    private Integer dropoffHubId;
    private String dropoffHubName;

    // ---------- Rates ----------
    private BigDecimal dailyRate;
    private BigDecimal weeklyRate;
    private BigDecimal monthlyRate;

    // ---------- Amounts ----------
    private BigDecimal vehicleAmount;
    private BigDecimal addonAmount;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;

    // ---------- Assigned vehicle (filled at hand-over) ----------
    private Integer assignedCarId;
    private String assignedCarRegistrationNo;
    private String assignedCarBrandName;
    private String assignedCarModelName;

    // ---------- Hand-over / return ----------
    private LocalDateTime handoverDate;
    private Integer fuelLevelOut;
    private Integer fuelLevelIn;
    private BigDecimal fuelCharges;

    // ---------- Add-ons ----------
    private List<BookingAddonResponse> addons = new ArrayList<>();

    public BookingResponse() {
    }

    // ---------- Booking ----------

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    // ---------- Customer ----------

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    // ---------- Address ----------

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public Integer getCityId() { return cityId; }
    public void setCityId(Integer cityId) { this.cityId = cityId; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public Integer getStateId() { return stateId; }
    public void setStateId(Integer stateId) { this.stateId = stateId; }

    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    // ---------- Vehicle ----------

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(Long carTypeId) {
        this.carTypeId = carTypeId;
    }

    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
    }

    public String getCarClass() {
        return carClass;
    }

    public void setCarClass(String carClass) {
        this.carClass = carClass;
    }

    public String getCarTypeImageUrl() {
        return carTypeImageUrl;
    }

    public void setCarTypeImageUrl(String carTypeImageUrl) {
        this.carTypeImageUrl = carTypeImageUrl;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    // ---------- Hubs ----------

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

    // ---------- Rates ----------

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

    // ---------- Amounts ----------

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

    // ---------- Assigned vehicle ----------

    public Integer getAssignedCarId() {
        return assignedCarId;
    }

    public void setAssignedCarId(Integer assignedCarId) {
        this.assignedCarId = assignedCarId;
    }

    public String getAssignedCarRegistrationNo() {
        return assignedCarRegistrationNo;
    }

    public void setAssignedCarRegistrationNo(String assignedCarRegistrationNo) {
        this.assignedCarRegistrationNo = assignedCarRegistrationNo;
    }

    public String getAssignedCarBrandName() {
        return assignedCarBrandName;
    }

    public void setAssignedCarBrandName(String assignedCarBrandName) {
        this.assignedCarBrandName = assignedCarBrandName;
    }

    public String getAssignedCarModelName() {
        return assignedCarModelName;
    }

    public void setAssignedCarModelName(String assignedCarModelName) {
        this.assignedCarModelName = assignedCarModelName;
    }

    // ---------- Hand-over / return ----------

    public LocalDateTime getHandoverDate() {
        return handoverDate;
    }

    public void setHandoverDate(LocalDateTime handoverDate) {
        this.handoverDate = handoverDate;
    }

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

    // ---------- Add-ons ----------

    public List<BookingAddonResponse> getAddons() {
        return addons;
    }

    public void setAddons(List<BookingAddonResponse> addons) {
        this.addons = addons;
    }
}

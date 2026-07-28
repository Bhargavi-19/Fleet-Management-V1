package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.demo.entity.base.BaseEntity;
import com.example.demo.enums.BookingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking_header")
public class BookingHeader extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    // Customer ID is String in your Customer entity
    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "car_id", nullable = false)
    private Long carId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "car_type_id")
    private Long carTypeId;

    @Column(name = "pickup_hub_id")
    private Integer pickupHubId;

    @Column(name = "pickup_hub_name")
    private String pickupHubName;

    @Column(name = "dropoff_hub_id")
    private Integer dropoffHubId;

    @Column(name = "dropoff_hub_name")
    private String dropoffHubName;

    // ---------------- CUSTOMER SNAPSHOT ----------------

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "driving_license_no")
    private String drivingLicenseNo;

    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "city_id")
    private Integer cityId;

    @Column(name = "state_id")
    private Integer stateId;

    @Column(name = "pincode")
    private String pincode;

    // ---------------- RATE SNAPSHOT ----------------

    @Column(name = "daily_rate", precision = 12, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "weekly_rate", precision = 12, scale = 2)
    private BigDecimal weeklyRate;

    @Column(name = "monthly_rate", precision = 12, scale = 2)
    private BigDecimal monthlyRate;

    // ---------------- BOOKING INFORMATION ----------------

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus bookingStatus;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "vehicle_amount", precision = 12, scale = 2)
    private BigDecimal vehicleAmount;

    @Column(name = "addon_amount", precision = 12, scale = 2)
    private BigDecimal addonAmount;

    @Column(name = "tax_amount", precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "grand_total", precision = 12, scale = 2)
    private BigDecimal grandTotal;
    
    @Column(name = "passport_no")
    private String passportNo;
 
    
 // =====================================================
 // Assigned Vehicle Information (Snapshot)
 // =====================================================

 @Column(name = "assigned_car_id")
 private Integer assignedCarId;

 @Column(name = "assigned_car_registration_no", length = 20)
 private String assignedCarRegistrationNo;

 @Column(name = "assigned_car_brand_name", length = 50)
 private String assignedCarBrandName;

 @Column(name = "assigned_car_model_name", length = 50)
 private String assignedCarModelName;

 @Column(name = "handover_date")
 private LocalDateTime handoverDate;

 @Column(name = "fuel_level_out")
 private Integer fuelLevelOut;
 
 @Column(name = "fuel_level_in")
 private Integer fuelLevelIn;

 @Column(name = "fuel_charges", precision = 10, scale = 2)
 private BigDecimal fuelCharges;

    public BookingHeader() {
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDateTime getDate() {
        return date;
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

	public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

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

    public Long getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(Long carTypeId) {
        this.carTypeId = carTypeId;
    }

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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDrivingLicenseNo() {
        return drivingLicenseNo;
    }

    public void setDrivingLicenseNo(String drivingLicenseNo) {
        this.drivingLicenseNo = drivingLicenseNo;
    }

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

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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
    
    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }
    
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
    
    
}
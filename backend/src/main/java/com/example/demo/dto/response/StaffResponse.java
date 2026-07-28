package com.example.demo.dto.response;

import java.time.LocalDate;

import com.example.demo.entity.base.Staff;

public class StaffResponse {

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String cityName;
    private String stateName;

    private Integer hubId;
    private String hubName;

    // Default Constructor
    public StaffResponse() {
    }

    // Parameterized Constructor
    public StaffResponse(Integer id,
                         String firstName,
                         String lastName,
                         String email,
                         String gender,
                         LocalDate dateOfBirth,
                         String phone,
                         String addressLine1,
                         String addressLine2,
                         String cityName,
                         String stateName,
                         Integer hubId,
                         String hubName) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.cityName = cityName;
        this.stateName = stateName;
        this.hubId = hubId;
        this.hubName = hubName;
    }

    // Static Mapper
    public static StaffResponse fromEntity(Staff staff) {

        return new StaffResponse(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getEmail(),
                staff.getGender(),
                staff.getDateOfBirth(),
                staff.getPhone(),
                staff.getAddressLine1(),
                staff.getAddressLine2(),
                staff.getCityName(),
                staff.getStateName(),
                staff.getHub().getHubId(),
                staff.getHub().getHubName()
        );
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Integer getHubId() {
        return hubId;
    }

    public void setHubId(Integer hubId) {
        this.hubId = hubId;
    }

    public String getHubName() {
        return hubName;
    }

    public void setHubName(String hubName) {
        this.hubName = hubName;
    }
}
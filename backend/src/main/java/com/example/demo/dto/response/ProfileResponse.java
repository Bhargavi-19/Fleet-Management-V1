package com.example.demo.dto.response;

<<<<<<< HEAD
=======
import java.time.LocalDate;

import com.example.demo.entity.base.Customer;
import com.example.demo.enums.DocumentType;

/**
 * Customer profile shown on the "My Profile" screen.
 */
>>>>>>> Developer
public class ProfileResponse {

    private String customerId;

    private String firstName;
<<<<<<< HEAD

    private String lastName;

    private String email; // Read Only

    private String phone;

    private String drivingLicenseNo;

    private String passportNo;

    public ProfileResponse() {
    }

=======
    private String lastName;

    private String email; // Read only
    private String phone;

    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;

    private String drivingLicenseNo;
    private String passportNo;

    private String addressLine1;
    private String addressLine2;

    private Integer cityId;
    private String cityName;

    private Integer stateId;
    private String stateName;

    private String pincode;

    private DocumentType documentType;

    public ProfileResponse() {
    }

    /** Convert an entity to this DTO. */
    public static ProfileResponse fromEntity(Customer customer) {

        ProfileResponse response = new ProfileResponse();

        response.setCustomerId(customer.getCustomerId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());

        response.setDateOfBirth(customer.getDateOfBirth());
        response.setGender(customer.getGender());
        response.setNationality(customer.getNationality());

        response.setDrivingLicenseNo(customer.getDrivingLicenseNo());
        response.setPassportNo(customer.getPassportNo());

        response.setAddressLine1(customer.getAddressLine1());
        response.setAddressLine2(customer.getAddressLine2());
        response.setPincode(customer.getPincode());
        response.setDocumentType(customer.getDocumentType());

        if (customer.getCity() != null) {
            response.setCityId(customer.getCity().getCityId());
            response.setCityName(customer.getCity().getCityName());
        }

        if (customer.getState() != null) {
            response.setStateId(customer.getState().getStateId());
            response.setStateName(customer.getState().getStateName());
        }

        return response;
    }

>>>>>>> Developer
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

<<<<<<< HEAD
=======
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

>>>>>>> Developer
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

<<<<<<< HEAD
}
=======
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

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}
>>>>>>> Developer

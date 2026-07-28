package com.example.demo.dto.response;

public class AssignVehicleResponse {

    private Long bookingId;

    private Integer assignedCarId;

    private String carNumber;

    private String carName;

    private String message;
    
    private String carRegistrationNo;

    private String brandName;

    private String modelName;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getAssignedCarId() {
        return assignedCarId;
    }

    public void setAssignedCarId(Integer assignedCarId) {
        this.assignedCarId = assignedCarId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getCarRegistrationNo() {
        return carRegistrationNo;
    }

    public void setCarRegistrationNo(String carRegistrationNo) {
        this.carRegistrationNo = carRegistrationNo;
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
}
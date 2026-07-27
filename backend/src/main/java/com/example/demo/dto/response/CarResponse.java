package com.example.demo.dto.response;

import java.time.LocalDate;

import com.example.demo.entity.base.Car;
import com.example.demo.enums.CarStatus;

public class CarResponse {

    private Integer carId;
    private String registrationNo;
    private String modelName;
    private String brandName;
    private Integer year;
    private String transmission;
    private Double mileage;
    private String fuelType;
    private Integer seatCapacity;
    private LocalDate serviceDueDate;
    private CarStatus status;
    private String image;
    private Integer carTypeId;
    private Integer hubId;
    private String hubName;

    // Default Constructor
    public CarResponse() {
    }

    // Parameterized Constructor
    public CarResponse(Integer carId,
                       String registrationNo,
                       String modelName,
                       String brandName,
                       Integer year,
                       String transmission,
                       Double mileage,
                       String fuelType,
                       Integer seatCapacity,
                       LocalDate serviceDueDate,
                       CarStatus status,
                       String image,
                       Integer carTypeId,
                       Integer hubId,
                       String hubName) {

        this.carId = carId;
        this.registrationNo = registrationNo;
        this.modelName = modelName;
        this.brandName = brandName;
        this.year = year;
        this.transmission = transmission;
        this.mileage = mileage;
        this.fuelType = fuelType;
        this.seatCapacity = seatCapacity;
        this.serviceDueDate = serviceDueDate;
        this.status = status;
        this.image = image;
        this.carTypeId = carTypeId;
        this.hubId = hubId;
        this.hubName = hubName;
    }

    // Convert Entity to Response DTO
    public static CarResponse fromEntity(Car car) {

        CarResponse response = new CarResponse();

        response.setCarId(car.getCarId());
        response.setRegistrationNo(car.getRegistrationNo());
        response.setModelName(car.getModelName());
        response.setBrandName(car.getBrandName());
        response.setYear(car.getYear());
        response.setTransmission(car.getTransmission());
        response.setMileage(car.getMileage());
        response.setFuelType(car.getFuelType());
        response.setSeatCapacity(car.getSeatCapacity());
        response.setServiceDueDate(car.getServiceDueDate());
        response.setStatus(car.getStatus());
        response.setImage(car.getImage());

        if (car.getCarType() != null) {
            response.setCarTypeId(car.getCarType().getCarTypeId());
        }

        if (car.getHub() != null) {
            response.setHubId(car.getHub().getHubId());
            response.setHubName(car.getHub().getHubName());
        }

        return response;
    }

    // Getters & Setters

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

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public Integer getSeatCapacity() {
        return seatCapacity;
    }

    public void setSeatCapacity(Integer seatCapacity) {
        this.seatCapacity = seatCapacity;
    }

    public LocalDate getServiceDueDate() {
        return serviceDueDate;
    }

    public void setServiceDueDate(LocalDate serviceDueDate) {
        this.serviceDueDate = serviceDueDate;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(Integer carTypeId) {
        this.carTypeId = carTypeId;
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
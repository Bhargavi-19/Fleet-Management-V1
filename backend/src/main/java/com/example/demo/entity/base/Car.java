package com.example.demo.entity.base;

import java.time.LocalDate;

import com.example.demo.entity.base.BaseEntity;
import com.example.demo.enums.CarStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "car")
public class Car extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Integer carId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_type_id", nullable = false)
    private CarType carType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_id", nullable =false)
    private Hub hub;

    @Column(name = "registration_no", nullable = false, unique = true, length = 20)
    private String registrationNo;

    @Column(name = "model_name", nullable = false, length = 50)
    private String modelName;

    @Column(name = "brand_name", nullable = false, length = 50)
    private String brandName;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "color", length = 30)
    private String color;

    @Column(name = "transmission", nullable = false, length = 20)
    private String transmission;

    @Column(name = "mileage")
    private Double mileage;

    @Column(name = "fuel_type", nullable = false, length = 20)
    private String fuelType;

    @Column(name = "fuel_capacity_liters")
    private Double fuelCapacityLiters;

    @Column(name = "seat_capacity")
    private Integer seatCapacity;

    @Column(name = "service_due_date")
    private LocalDate serviceDueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CarStatus status;

    @Column(name = "image", length = 255)
    private String image;

    public Car() {
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public Hub getHub() {
        return hub;
    }

    public void setHub(Hub hub) {
        this.hub = hub;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public Double getFuelCapacityLiters() {
        return fuelCapacityLiters;
    }

    public void setFuelCapacityLiters(Double fuelCapacityLiters) {
        this.fuelCapacityLiters = fuelCapacityLiters;
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

}
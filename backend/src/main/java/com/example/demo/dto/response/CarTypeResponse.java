package com.example.demo.dto.response;

import com.example.demo.entity.base.CarType;
import com.example.demo.enums.CarClass;

public class CarTypeResponse {

	private Integer carTypeId;
    private CarClass carClass;
    private String carType;
    private String imageUrl;
    private Double dailyRate;
    private Double weeklyRate;
    private Double monthlyRate;
	private Integer hubId;
	private String hubName;

    // Default Constructor
    public CarTypeResponse() {
    }

    public CarTypeResponse(Integer carTypeId,
            CarClass carClass,
            String carType,
            String imageUrl,
            Double dailyRate,
            Double weeklyRate,
            Double monthlyRate,
            Integer hubId,
            String hubName) {

        this.carTypeId = carTypeId;
        this.carClass = carClass;
        this.carType = carType;
        this.imageUrl = imageUrl;
        this.dailyRate = dailyRate;
        this.weeklyRate = weeklyRate;
        this.monthlyRate = monthlyRate;
        this.hubId = hubId;
        this.hubName = hubName;
    }

    // Convert Entity to Response DTO
    public static CarTypeResponse fromEntity(CarType carType) {

    	CarTypeResponse response = new CarTypeResponse();

        response.setCarTypeId(carType.getCarTypeId());
        response.setCarClass(carType.getCarClass());
        response.setCarType(carType.getCarType());
        response.setImageUrl(carType.getImageUrl());
        response.setDailyRate(carType.getDailyRate());
        response.setWeeklyRate(carType.getWeeklyRate());
        response.setMonthlyRate(carType.getMonthlyRate());

        if (carType.getHub() != null) {
            response.setHubId(carType.getHub().getHubId());
            response.setHubName(carType.getHub().getHubName());
        }

        return response;
    }

    // Getters & Setters
    
    public Integer getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(Integer carTypeId) {
        this.carTypeId = carTypeId;
    }

    public CarClass getCarClass() {
        return carClass;
    }

    public void setCarClass(CarClass carClass) {
        this.carClass = carClass;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(Double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Double getWeeklyRate() {
        return weeklyRate;
    }

    public void setWeeklyRate(Double weeklyRate) {
        this.weeklyRate = weeklyRate;
    }

    public Double getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(Double monthlyRate) {
        this.monthlyRate = monthlyRate;
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
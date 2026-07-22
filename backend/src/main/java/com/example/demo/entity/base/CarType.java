package com.example.demo.entity.base;

import java.time.LocalDate;

import com.example.demo.enums.CarClass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "car_type")
public class CarType extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "car_type_id")
	private Integer carTypeId;
	
	 @Enumerated(EnumType.STRING)
	 @Column(name = "car_class", nullable = false)
	 private CarClass carClass;

	 @Column(name = "car_type")
	 private String carType;

	 @Column(name = "image_url")
	 private String imageUrl;

	 @Column(name = "daily_rate")
	 private Double dailyRate;

	 @Column(name = "weekly_rate")
	 private Double weeklyRate;

	 @Column(name = "monthly_rate")
	 private Double monthlyRate;

	 @Column(name = "hub_id")
	 private Integer hubId;

	 @Column(name = "effective_from")
	 private LocalDate effectiveFrom;

	 @Column(name = "effective_to")
	 private LocalDate effectiveTo;

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

	 public LocalDate getEffectiveFrom() {
		 return effectiveFrom;
	 }

	 public void setEffectiveFrom(LocalDate effectiveFrom) {
		 this.effectiveFrom = effectiveFrom;
	 }

	 public LocalDate getEffectiveTo() {
		 return effectiveTo;
	 }

	 public void setEffectiveTo(LocalDate effectiveTo) {
		 this.effectiveTo = effectiveTo;
	 }
	 
	 public Integer getCarTypeId() {
		return carTypeId;
	}

	 public void setCarTypeId(Integer carTypeId) {
		 this.carTypeId = carTypeId;
	 }

}

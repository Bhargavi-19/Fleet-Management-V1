package com.example.demo.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.base.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
	
	List<Car> findByHub_HubId(Integer hubId);

    List<Car> findByHub_HubIdAndCarType_CarTypeId(Integer hubId, Integer carTypeId);

}

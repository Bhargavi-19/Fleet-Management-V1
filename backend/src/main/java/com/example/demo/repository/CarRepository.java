package com.example.demo.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.base.Car;
<<<<<<< HEAD
=======
import com.example.demo.enums.CarStatus;
>>>>>>> Developer

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
	
	List<Car> findByHub_HubId(Integer hubId);

<<<<<<< HEAD
    List<Car> findByHub_HubIdAndCarType_CarTypeId(Integer hubId, Integer carTypeId);

=======
    List<Car> findByHub_HubIdAndStatus(Integer hubId, CarStatus status);

    List<Car> findByHub_HubIdAndCarType_CarTypeId(Integer hubId, Integer carTypeId);

    /**
     * Vehicles of one category at one hub in a given state - used at hand-over
     * to offer staff only the AVAILABLE cars of the type the customer booked.
     */
    List<Car> findByHub_HubIdAndCarType_CarTypeIdAndStatus(
            Integer hubId, Integer carTypeId, CarStatus status);

>>>>>>> Developer
}

package com.example.demo.repository;

import java.util.List;
<<<<<<< HEAD
=======
import java.util.Optional;
>>>>>>> Developer

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.base.CarType;
<<<<<<< HEAD
=======
import com.example.demo.enums.CarClass;
>>>>>>> Developer

@Repository
public interface CarTypeRepository extends JpaRepository<CarType, Integer> {

    List<CarType> findByHub_HubId(Integer hubId);
<<<<<<< HEAD
=======

    /**
     * One rate row per car class per hub - used by the Excel rate upload to
     * decide whether to update an existing rate or insert a new one.
     */
    Optional<CarType> findByHub_HubIdAndCarClass(Integer hubId, CarClass carClass);
>>>>>>> Developer
    
    @Query("""
            SELECT c.status, COUNT(c)
            FROM Car c
            GROUP BY c.status
           """)
    List<Object[]> countCarsByStatus();

}
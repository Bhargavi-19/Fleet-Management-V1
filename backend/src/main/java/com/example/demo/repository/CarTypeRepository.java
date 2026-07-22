package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.base.CarType;

@Repository
public interface CarTypeRepository extends JpaRepository<CarType, Long> {

    List<CarType> findByHubId(Integer hubId);

}

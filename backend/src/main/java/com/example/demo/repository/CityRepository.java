package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.base.City;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

    List<City> findByState_StateId(Integer stateId);

}
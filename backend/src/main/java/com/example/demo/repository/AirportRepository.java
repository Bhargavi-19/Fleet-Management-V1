package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.base.Airport;

public interface AirportRepository extends JpaRepository<Airport, Integer> {

    // Find airports by City ID
    List<Airport> findByCity_CityId(Integer cityId);

    // Find airports by State ID and City ID
    List<Airport> findByState_StateIdAndCity_CityId(Integer stateId, Integer cityId);

    // Search airport by city name, airport name or airport code
    @Query(value = """
            SELECT a.*
            FROM airport a
            INNER JOIN city c ON a.city_id = c.city_id
            WHERE LOWER(c.city_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(a.airport_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(a.airport_code) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY c.city_name, a.airport_name
            """, nativeQuery = true)
    List<Airport> searchAirport(@Param("keyword") String keyword);

}
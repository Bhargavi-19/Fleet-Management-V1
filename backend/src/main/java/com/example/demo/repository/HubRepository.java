package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.base.Hub;

@Repository
public interface HubRepository extends JpaRepository<Hub, Integer> {

    Optional<Hub> findByHubId(Integer hubId);

    List<Hub> findByStateIdAndCityId(Integer stateId, Integer cityId);

}
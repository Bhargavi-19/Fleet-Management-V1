package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.base.Addon;

@Repository
public interface AddonRepository extends JpaRepository<Addon, Integer> {

    List<Addon> findByHub_HubId(Integer hubId);

}
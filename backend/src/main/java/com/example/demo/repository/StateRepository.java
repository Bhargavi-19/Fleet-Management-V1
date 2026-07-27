package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.base.State;


@Repository
public interface StateRepository extends JpaRepository<State, Integer> {

    List<State> findAllByOrderByStateNameAsc();

}
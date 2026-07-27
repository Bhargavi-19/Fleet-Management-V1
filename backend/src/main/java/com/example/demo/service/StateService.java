package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.response.StateResponse;
import com.example.demo.entity.base.State;

public interface StateService {

    List<StateResponse> getAllStates();

}
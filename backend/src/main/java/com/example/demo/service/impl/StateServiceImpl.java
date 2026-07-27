package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.StateResponse;
import com.example.demo.entity.base.State;
import com.example.demo.exception.error.ResourceNotFoundException;
import com.example.demo.repository.StateRepository;
import com.example.demo.service.StateService;

@Service
public class StateServiceImpl implements StateService {

    private final StateRepository repository;

    public StateServiceImpl(StateRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<StateResponse> getAllStates() {

        return repository.findAll()
                .stream()
                .map(StateResponse::fromEntity)
                .toList();
    }
}

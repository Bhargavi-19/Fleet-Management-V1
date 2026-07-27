package com.example.demo.dto.response;

import com.example.demo.entity.base.State;

public class StateResponse {

    private Integer stateId;
    private String stateName;

    public StateResponse() {
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public static StateResponse fromEntity(State state) {

        StateResponse response = new StateResponse();

        response.setStateId(state.getStateId());
        response.setStateName(state.getStateName());

        return response;
    }
}
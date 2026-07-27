package com.example.demo.dto.response;

import com.example.demo.enums.CarStatus;

public class CarStatusCountResponse {

    private CarStatus status;
    private Long count;

    public CarStatusCountResponse() {
    }

    public CarStatusCountResponse(CarStatus result, Long count) {
        this.status = result;
        this.count = count;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
package com.example.demo.dto.response;

import com.example.demo.entity.base.Airport;

public class AirportResponse {

    private Integer airportId;
    private String airportCode;
    private String airportName;
    private Integer hubId;

    public AirportResponse() {
    }

    public Integer getAirportId() {
        return airportId;
    }

    public void setAirportId(Integer airportId) {
        this.airportId = airportId;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public Integer getHubId() {
        return hubId;
    }

    public void setHubId(Integer hubId) {
        this.hubId = hubId;
    }

    public static AirportResponse fromEntity(Airport airport) {

        AirportResponse response = new AirportResponse();

        response.setAirportId(airport.getAirportId());
        response.setAirportCode(airport.getAirportCode());
        response.setAirportName(airport.getAirportName());

        if (airport.getHub() != null) {
            response.setHubId(airport.getHub().getHubId());
        }

        return response;
    }
}
package com.example.demo.dto.response;

import com.example.demo.entity.base.City;

public class CityResponse {

    private Integer cityId;
    private String cityName;

    public CityResponse() {
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public static CityResponse fromEntity(City city) {

        CityResponse response = new CityResponse();

        response.setCityId(city.getCityId());
        response.setCityName(city.getCityName());

        return response;
    }
}

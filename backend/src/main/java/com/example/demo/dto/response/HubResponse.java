package com.example.demo.dto.response;

import com.example.demo.entity.base.Hub;
import com.example.demo.enums.HubType;

public class HubResponse {

    private Integer hubId;

    private String hubName;

    private HubType hubType;

    private String addressLine1;

    private String addressLine2;

    private Integer cityId;
    private String cityName;

    private Integer stateId;
    private String stateName;

    private String pincode;

    private String phone;

    public HubResponse() {
    }

    public Integer getHubId() {
        return hubId;
    }

    public void setHubId(Integer hubId) {
        this.hubId = hubId;
    }

    public String getHubName() {
        return hubName;
    }

    public void setHubName(String hubName) {
        this.hubName = hubName;
    }

    public HubType getHubType() {
        return hubType;
    }

    public void setHubType(HubType hubType) {
        this.hubType = hubType;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static HubResponse fromEntity(Hub hub) {

        HubResponse response = new HubResponse();

        response.setHubId(hub.getHubId());
        response.setHubName(hub.getHubName());
        response.setHubType(hub.getHubType());
        response.setAddressLine1(hub.getAddressLine1());
        response.setAddressLine2(hub.getAddressLine2());

        if (hub.getCity() != null) {

            response.setCityId(hub.getCity().getCityId());
            response.setCityName(hub.getCity().getCityName());

            if (hub.getCity().getState() != null) {
                response.setStateId(hub.getCity().getState().getStateId());
                response.setStateName(hub.getCity().getState().getStateName());
            }
        }

        response.setPincode(hub.getPincode());
        response.setPhone(hub.getPhone());

        return response;
    }
}
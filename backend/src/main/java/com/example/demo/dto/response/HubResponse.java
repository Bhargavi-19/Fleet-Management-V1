package com.example.demo.dto.response;

import com.example.demo.entity.base.CarType;
import com.example.demo.entity.base.Hub;
import com.example.demo.enums.HubType;

public class HubResponse {
	
	private Integer hubId;

    private String hubName;

    private HubType hubType;

    private String addressLine1;

    private String addressLine2;

    private Integer cityId;

    private Integer stateId;

    private String pincode;

    private String phone;

	public HubResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HubResponse(Integer hubId, String hubName, HubType hubType, String addressLine1, String addressLine2,
			Integer cityId, Integer stateId, String pincode, String phone) {
		super();
		this.hubId = hubId;
		this.hubName = hubName;
		this.hubType = hubType;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.cityId = cityId;
		this.stateId = stateId;
		this.pincode = pincode;
		this.phone = phone;
	}

	public Integer getHubId() {
		return hubId;
	}

	public void setHubId(Integer integer) {
		this.hubId = integer;
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

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
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
	    response.setCityId(hub.getCityId());
	    response.setStateId(hub.getStateId());
	    response.setPincode(hub.getPincode());
	    response.setPhone(hub.getPhone());

	    return response;
	}
    
}

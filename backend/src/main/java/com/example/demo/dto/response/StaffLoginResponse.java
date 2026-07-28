package com.example.demo.dto.response;

public class StaffLoginResponse {

    private String token;
    private String tokenType;
    private Integer staffId;
    private String firstName;
    private String lastName;
    private String email;
    private Integer hubId;

    // Default Constructor
    public StaffLoginResponse() {
    }

    // Parameterized Constructor
    public StaffLoginResponse(String token, String tokenType,
                         Integer staffId, String firstName,
                         String lastName, String email,
                         Integer hubId) {
        this.token = token;
        this.tokenType = tokenType;
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hubId = hubId;
    }

    // Getters and Setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getHubId() {
        return hubId;
    }

    public void setHubId(Integer hubId) {
        this.hubId = hubId;
    }
}

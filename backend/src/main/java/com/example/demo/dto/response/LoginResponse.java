package com.example.demo.dto.response;

public class LoginResponse {

    private String accessToken;

    private String tokenType;

    private String customerId;

    private String email;

    public LoginResponse() {
    }

    public LoginResponse(String accessToken,
                         String tokenType,
                         String customerId,
                         String email) {

        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.customerId = customerId;
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
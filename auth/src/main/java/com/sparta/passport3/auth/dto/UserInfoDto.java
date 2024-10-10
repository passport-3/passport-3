package com.sparta.passport3.auth.dto;

public class UserInfoDto {

    private String userId;
    private String role;

    public UserInfoDto(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }


}

package com.sparta.passport3.auth.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.annotation.processing.Generated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @JsonProperty("username")
    private String username;

    @JsonProperty("token")
    private String token;

    @JsonProperty("expireTime")
    private long expireTime;

    @Builder(builderClassName = "RefreshTokenBuilder", builderMethodName = "RefreshTokenBuilder")
    public RefreshToken(String username, String token, long expireTime) {
        this.username = username;
        this.token = token;
        this.expireTime = expireTime;
    }
}

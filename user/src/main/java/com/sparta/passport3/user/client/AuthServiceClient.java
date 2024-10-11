package com.sparta.passport3.user.client;

import com.sparta.passport3.user.dto.TokenResponseDto;
import com.sparta.passport3.user.dto.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @PostMapping("/api/auth/login")
    ResponseEntity<TokenResponseDto> login(@RequestBody UserInfoDto userInfoDto);

}

package com.sparta.passport3.auth.client;

import com.sparta.passport3.auth.dto.UserInfoDto;
import com.sparta.passport3.auth.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/user/{username}")
    ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable("username") String username);

    @GetMapping("/api/user/{username}/info")
    ResponseEntity<UserInfoDto> getUserInfoByUsername(@PathVariable String username);
}


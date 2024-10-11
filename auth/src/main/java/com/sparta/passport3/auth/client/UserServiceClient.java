package com.sparta.passport3.auth.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "user-service")
public interface UserServiceClient {

}


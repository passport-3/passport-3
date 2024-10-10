package com.sparta.passport3.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.passport3.auth.model.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RefreshTokenService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveTokenData(String key, RefreshToken tokenData) throws JsonProcessingException {
        String jsonData = objectMapper.writeValueAsString(tokenData);
        redisTemplate.opsForValue().set(key, jsonData, tokenData.getExpireTime(), TimeUnit.MILLISECONDS);
    }

    public RefreshToken getTokenData(String key) throws JsonProcessingException {
        String jsonData = redisTemplate.opsForValue().get(key);
        return objectMapper.readValue(jsonData, RefreshToken.class);
    }

    public void deleteTokenData(String key) {
        redisTemplate.delete(key);
    }
}

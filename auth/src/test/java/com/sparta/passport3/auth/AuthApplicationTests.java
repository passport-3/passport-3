package com.sparta.passport3.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.passport3.auth.model.RefreshToken;
import com.sparta.passport3.auth.service.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
class AuthApplicationTests {

    private RedisService redisService;
    private RedisTemplate<String, String> redisTemplate;
    private ObjectMapper objectMapper;


    @Test
    public void testRedisConnection() {
        //assertNotNull(redisTemplate.getConnectionFactory());
        String response = redisTemplate.getConnectionFactory().getConnection().ping();
        System.out.println("Redis PING response: " + response);
    }


    @BeforeEach
    public void setUp() {
        redisTemplate = Mockito.mock(RedisTemplate.class);
        objectMapper = new ObjectMapper();
        redisService = new RedisService(redisTemplate, objectMapper);
    }

    @Test
    public void testSaveTokenData() throws JsonProcessingException {
        RefreshToken tokenData = new RefreshToken();
        tokenData.setUsername("testUser");
        tokenData.setToken("testToken");
        tokenData.setExpireTime(3600000); // 1 hour in milliseconds

        String key = "testUser";
        String tokenDataJson = objectMapper.writeValueAsString(tokenData); // Serialize tokenData to JSON

        doNothing().when(redisTemplate.opsForValue()).set(eq(key), eq(tokenDataJson), eq(3600000L), any());

        // Token 저장 메서드 호출
        redisService.saveTokenData(key, tokenData);

        verify(redisTemplate.opsForValue()).set(eq(key), eq(tokenDataJson), eq(3600000L), any());
    }
}

package com.sparta.passport3.auth.service;

import com.sparta.passport3.auth.client.UserServiceClient;
import com.sparta.passport3.auth.dto.LoginRequestDto;
import com.sparta.passport3.auth.dto.UserInfoDto;
import com.sparta.passport3.auth.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserServiceClient userServiceClient;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserServiceClient userServiceClient) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userServiceClient = userServiceClient;
    }

    // 로그인시 정보조회
    public String login(LoginRequestDto loginRequest, HttpServletRequest request) {
        // 1. User 서비스로부터 사용자 정보 조회 (DTO 형식으로 ID와 Role만 받음)
        ResponseEntity<com.sparta.passport3.auth.dto.UserInfoDto> userResponse = userServiceClient.getUserInfoByUsername(loginRequest.getUsername());
        if (userResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        UserInfoDto userInfo = userResponse.getBody();

        // 2. 인증 처리 (Spring Security를 사용하여 사용자 인증)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // 3. 인증이 완료되면 JWT 토큰 발급 (ID와 Role을 사용)
        return jwtTokenUtil.createToken(userInfo.getUserId(), userInfo.getRole(), request);
    }
}


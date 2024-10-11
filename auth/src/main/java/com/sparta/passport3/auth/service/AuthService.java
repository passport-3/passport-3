package com.sparta.passport3.auth.service;

import com.sparta.passport3.auth.client.UserServiceClient;
import com.sparta.passport3.auth.dto.LoginRequestDto;
import com.sparta.passport3.auth.dto.UserInfoDto;
import com.sparta.passport3.auth.jwt.JwtTokenUtil;
import com.sparta.passport3.auth.type.Const;
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
    private final JwtTokenUtil jwtTokenUtil;


    public AuthService(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    // 로그인
    public String login(UserInfoDto userInfoDto) {
        // 1. 사용자 정보 확인 (UserInfoDto에 사용자 ID 및 역할 정보 포함)
        String userId = userInfoDto.getUserId();
        String role = userInfoDto.getRole();

//        // 2. JWT 토큰 생성
//        String accessToken = jwtTokenUtil.createToken();
//        String refreshToken = jwtTokenUtil.createToken();



//        return accessToken; // access token 반환
        return null;
    }
}


package com.sparta.passport3.auth.controller;

import com.sparta.passport3.auth.client.UserServiceClient;
import com.sparta.passport3.auth.dto.TokenResponseDto;
import com.sparta.passport3.auth.dto.UserResponseDto;
import com.sparta.passport3.auth.jwt.JwtTokenUtil;
import com.sparta.passport3.auth.security.UserDetailsImpl;
import com.sparta.passport3.auth.dto.LoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserServiceClient userServiceClient;  // Feign Client로 User 서비스와 통신
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserServiceClient userServiceClient, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userServiceClient = userServiceClient;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 1. User 서비스에서 사용자 정보 및 역할 받아오기
        ResponseEntity<UserResponseDto> userResponse = userServiceClient.getUserByUsername(userDetails.getUsername());
        if (userResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("사용자 정보 불러오기 실패");
        }

        String role = userResponse.getBody().getRole();

        // HttpServletRequest를 함께 전달
        String token = jwtTokenUtil.createToken(userDetails.getUsername(), role, request);

        return ResponseEntity.ok(new TokenResponseDto(token));
    }

    @PostMapping("/encodePassword")
    public ResponseEntity<String> encodePassword(@RequestBody String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return ResponseEntity.ok(encodedPassword);
    }
}


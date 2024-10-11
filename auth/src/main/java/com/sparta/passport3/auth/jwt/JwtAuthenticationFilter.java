package com.sparta.passport3.auth.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.passport3.auth.client.UserServiceClient;
import com.sparta.passport3.auth.dto.LoginRequestDto;
import com.sparta.passport3.auth.dto.UserResponseDto;
import com.sparta.passport3.auth.model.RefreshToken;
import com.sparta.passport3.auth.security.UserDetailsImpl;
import com.sparta.passport3.auth.service.RefreshTokenService;
import com.sparta.passport3.auth.type.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private JwtTokenUtil jwtTokenUtil;
    private UserServiceClient userServiceClient;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public JwtAuthenticationFilter(JwtTokenUtil JwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userServiceClient = userServiceClient;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();


        // 1. User 서비스로부터 사용자 역할 정보를 가져옴
        ResponseEntity<UserResponseDto> userResponse = userServiceClient.getUserByUsername(username);

        if (userResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("사용자 정보 불러오기 실패");
        }

        // 2. 역할 정보를 String으로 받아옴
        String role = userResponse.getBody().getRole();

        // 3. JWT 토큰 생성
        String accessToken = jwtTokenUtil.createToken(Const.ACCESS_TOKEN, username, role, Const.ACCESS_TOKEN_EXPIRES_IN); // 10분
        String refreshToken = jwtTokenUtil.createToken(Const.REFRESH_TOKEN, username, role, Const.REFRESH_TOKEN_EXPIRES_IN); // 24시간

        // access token -> header
        response.setHeader(Const.ACCESS_TOKEN, accessToken);
        // refresh token -> cookie
        jwtTokenUtil.addJwtToCookie(refreshToken, response);
        saveRefreshToken(username, refreshToken, Const.REFRESH_TOKEN_EXPIRES_IN);
        response.setStatus(HttpServletResponse.SC_OK);

    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }

    // refresh token 저장
    private void saveRefreshToken(String username, String refreshToken, Long expireTime) throws JsonProcessingException {
        RefreshToken tokenData = RefreshToken.RefreshTokenBuilder()
                .username(username)
                .token(refreshToken)
                .expireTime(System.currentTimeMillis() + expireTime)
                .build();
        String key = this.generateKey(username);

        try {
            refreshTokenService.saveTokenData(key, tokenData);
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }

    private String generateKey(String username) {
        String tokenId = UUID.randomUUID().toString(); // 고유한 토큰 ID 생성
        return String.format("user:%s:token:%s", username, tokenId);
    }

}



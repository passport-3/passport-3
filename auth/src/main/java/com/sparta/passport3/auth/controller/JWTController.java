package com.sparta.passport3.auth.controller;

import com.sparta.passport3.auth.jwt.JwtTokenUtil;
import com.sparta.passport3.auth.type.UserRoleEnum;
import com.sparta.passport3.auth.type.Const;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class JWTController {

    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/refresh")
    public ResponseEntity<?> tokenRefresh(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals(Const.REFRESH_TOKEN)) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // expired check


        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtTokenUtil.getCategoryFromToken(refresh);

        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtTokenUtil.getUsernameFromToken(refresh);
        UserRoleEnum role = jwtTokenUtil.getRoleFromToken(refresh);

        // new access token
        String newAccess = jwtTokenUtil.createToken(Const.ACCESS_TOKEN, username, String.valueOf(role), Const.ACCESS_TOKEN_EXPIRES_IN);

        response.setHeader(Const.ACCESS_TOKEN, newAccess);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}



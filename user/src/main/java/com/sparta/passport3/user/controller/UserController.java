package com.sparta.passport3.user.controller;

import com.sparta.passport3.user.dto.*;
import com.sparta.passport3.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 클라이언트로부터의 로그인 요청을 받음
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto) {

        // UserService에서 사용자 정보 생성 후 Auth 서비스에 전달
        String message = userService.login(loginRequestDto.getUsername(), loginRequestDto.getPassword());
        // 성공 메시지 반환
        return ResponseEntity.ok(message);
    }

    // 회원가입 엔드포인트
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto signupRequestDto) {
        userService.signUp(signupRequestDto);  // 회원가입 서비스 호출
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }


}

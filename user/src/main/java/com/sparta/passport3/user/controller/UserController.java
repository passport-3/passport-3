package com.sparta.passport3.user.controller;

import com.sparta.passport3.user.dto.LoginRequestDto;
import com.sparta.passport3.user.dto.SignUpRequestDto;
import com.sparta.passport3.user.dto.UserInfoDto;
import com.sparta.passport3.user.dto.UserResponseDto;
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

    // 사용자 정보 가져오기
    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        UserResponseDto userResponse = userService.findByUsername(username);
        return ResponseEntity.ok(userResponse);
    }

//    // login
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequest) {
//        String token = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
//        return ResponseEntity.ok(token);
//    }

    // 회원가입
    @PostMapping("/signUp")
    public ResponseEntity<String> signup(
            @RequestBody @Valid SignUpRequestDto requestDto,
            BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors()) {
            String message=bindingResult.getFieldError().getDefaultMessage();
            log.error(message);
            ResponseEntity.ok(message);

        }
        userService.signUp(requestDto);
        return ResponseEntity.ok("signUp successfully");
    }

    // 사용자 정보 조회 (UserInfoDto 반환)
    @GetMapping("/{username}/info")
    public ResponseEntity<UserInfoDto> getUserInfoByUsername(@PathVariable String username) {
        UserInfoDto userInfoDto = userService.getUserInfoByUsername(username);
        return ResponseEntity.ok(userInfoDto);
    }
}

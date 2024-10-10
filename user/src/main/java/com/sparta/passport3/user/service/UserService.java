package com.sparta.passport3.user.service;

import com.sparta.passport3.user.dto.*;
import com.sparta.passport3.user.client.AuthServiceClient;
import com.sparta.passport3.user.model.User;
import com.sparta.passport3.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final AuthServiceClient authServiceClient;
    private final UserRepository userRepository;

    public UserService(AuthServiceClient authServiceClient, UserRepository userRepository) {
        this.authServiceClient = authServiceClient;
        this.userRepository = userRepository;
    }

    public UserResponseDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        // User 엔티티를 UserResponseDto로 변환
        // UserRoleEnum을 문자열로 변환하여 UserResponseDto로 반환
        return new UserResponseDto(user.getUsername(), user.getRole().name());
    }

    // 회원가입
    public void signUp(@Valid SignUpRequestDto user) {
        // 사용자 중복 확인
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("사용자가 이미 존재합니다.");
        }

        // 비밀번호 암호화
        ResponseEntity<String> response = authServiceClient.encodePassword(user.getPassword());
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("비밀번호 암호화 실패");
        }
        String encodedPassword = response.getBody();

        // 새 User 객체 생성
        User User = new User(user.getUsername(), encodedPassword, user.getEmail(), user.getPhone(), user.getRole());

        // 사용자 저장
        userRepository.save(User);
    }

    // 로그인시 사용자 정보를 조회한다.
    public UserInfoDto getUserInfoByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        // 사용자 ID와 역할을 UserInfoDto로 묶어서 반환
        return new UserInfoDto(user.getUserId().toString(), user.getRole().name());
    }


//    // 로그인
//    public String login(String username, String password) {
//        // 사용자 정보 조회
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        // 사용자 ID와 역할을 UserRoleIdDto로 묶어서 Auth 서비스로 전달
//        UserInfoDto UserInfoDto = new UserInfoDto(user.getUserId().toString() , user.getRole().name());
//
//        // Auth 서비스에 로그인 요청 (토큰 발급)
//        ResponseEntity<TokenResponseDto> response = authServiceClient.login(UserInfoDto);
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            // Auth 서비스에서 받은 토큰 반환
//            return response.getBody().getToken();
//        } else {
//            throw new RuntimeException("로그인 실패");
//        }
//    }




}



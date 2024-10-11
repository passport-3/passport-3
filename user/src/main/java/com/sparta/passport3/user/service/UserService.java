package com.sparta.passport3.user.service;

import com.sparta.passport3.user.dto.*;
import com.sparta.passport3.user.client.AuthServiceClient;
import com.sparta.passport3.user.model.User;
import com.sparta.passport3.user.model.UserRoleEnum;
import com.sparta.passport3.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final AuthServiceClient authServiceClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AuthServiceClient authServiceClient, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authServiceClient = authServiceClient;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 처리
    public void signUp(SignUpRequestDto signupRequestDto) {
        // 1. 사용자 정보 중복 체크
        if (userRepository.findByUsername(signupRequestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if(userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미사용중인 이메일입니다.");
        }

        // 2. 비밀번호 해싱 처리
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        // 3. User 엔티티 생성 (role은 기본적으로 USER로 설정)
        User user = User.create(
                signupRequestDto.getUsername(),
                encodedPassword,  // 해싱된 비밀번호 사용
                signupRequestDto.getEmail(),
                signupRequestDto.getPhone(),
                UserRoleEnum.USER  // 기본 역할 설정
        );

        // 4. User 저장
        userRepository.save(user);
    }


    // 로그인
    public String login(String username, String password) {
        // 1. 사용자 정보 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원은 존재하지 않습니다."));

        // 2. 사용자 ID와 역할을 UserInfoDto로 묶어서 Auth 서비스로 전달
        UserInfoDto UserInfoDto = new UserInfoDto(user.getUserId().toString(), user.getRole().name());

        // 3. Auth 서비스에 객체를 넘겨 로그인을 요청한다. (토큰 발급을 요청한다.)
        ResponseEntity<TokenResponseDto> response = authServiceClient.login(UserInfoDto);

        // 4. 응답 성공 여부 확인 (토큰 관련 작업은 AuthService에서 처리)
        if (response.getStatusCode().is2xxSuccessful()) {
            return "로그인 성공"; // 성공 응답 반환
        } else {
            throw new IllegalStateException("로그인 실패");
        }
    }

    }








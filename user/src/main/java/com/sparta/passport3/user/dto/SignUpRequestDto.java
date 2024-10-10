package com.sparta.passport3.user.dto;

import com.sparta.passport3.user.model.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

    @NotBlank
    @Size(min=4,max=10)
    private String username;

    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min=13,max=13)
    private String phone;

    private UserRoleEnum role; // 사용자 역할
}

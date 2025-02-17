package com.barogo.api.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "id를 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Builder
    public LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}

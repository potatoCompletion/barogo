package com.barogo.api.auth.controller;

import com.barogo.api.auth.request.LoginRequest;
import com.barogo.api.auth.request.SignUpRequest;
import com.barogo.api.auth.response.SignUpResponse;
import com.barogo.api.auth.response.TokenResponse;
import com.barogo.api.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API
     * @param signUpRequest 회원가입 요청 정보
     * @return 회원가입 성공 메세지
     */
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SignUpResponse("회원가입이 완료되었습니다."));
    }

    /**
     * 로그인 API
     * @param loginRequest 로그인 요청 정보
     * @return 로그인 성공 메세지
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}

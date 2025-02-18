package com.barogo.api.auth.service;

import com.barogo.api.auth.exception.InvalidCredentialException;
import com.barogo.api.auth.exception.InvalidPasswordException;
import com.barogo.api.auth.exception.PasswordPolicyViolationException;
import com.barogo.api.auth.exception.UserIdDuplicatedException;
import com.barogo.api.auth.request.LoginRequest;
import com.barogo.api.auth.request.SignUpRequest;
import com.barogo.api.auth.response.TokenResponse;
import com.barogo.api.common.util.PasswordValidator;
import com.barogo.api.user.domain.User;
import com.barogo.api.user.repository.UserRepository;
import com.barogo.api.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void signUp(SignUpRequest signUpRequest) {
        // 중복 ID 검증
        if (userRepository.existsByUserId(signUpRequest.getUserId())) {
            throw new UserIdDuplicatedException();
        }

        // 패스워드 규칙 검증
        if (!PasswordValidator.isValid(signUpRequest.getPassword())) {
            throw new PasswordPolicyViolationException();
        }

        User user = signUpRequest.toUser(passwordEncoder);
        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest loginRequest) {
        // 유저 찾기
        User user = userRepository.findByUserId(loginRequest.getUserId())
                .orElseThrow(InvalidCredentialException::new);

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        // 액세스 토큰 발급
        String accessToken = jwtTokenProvider.generateToken(loginRequest.getUserId());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}

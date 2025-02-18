package com.barogo.api.auth.service;

import com.barogo.api.auth.exception.InvalidPasswordException;
import com.barogo.api.auth.exception.PasswordPolicyViolationException;
import com.barogo.api.auth.exception.UserIdDuplicatedException;
import com.barogo.api.auth.exception.InvalidCredentialException;
import com.barogo.api.auth.request.LoginRequest;
import com.barogo.api.auth.request.SignUpRequest;
import com.barogo.api.auth.response.TokenResponse;
import com.barogo.api.user.domain.User;
import com.barogo.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 - 정상 요청")
    void 회원가입_테스트() {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .userId("testId")
                .password("testPassword123!@#")
                .name("김완수")
                .build();

        // when
        authService.signUp(request);
        User user = userRepository.findAll().get(0);

        //then
        assertEquals(1, userRepository.count());
        assertEquals("testId", user.getUserId());
        assertTrue(passwordEncoder.matches("testPassword123!@#", user.getPassword()));
        assertEquals("김완수", user.getName());
    }

    @Test
    @DisplayName("회원가입 - 중복 아이디(UserIdDuplicatedException)")
    void 중복_아이디_회원가입_테스트() {
        // given
        User user = User.builder()
                .userId("testId")
                .password(passwordEncoder.encode("testPassword"))
                .name("김완수")
                .build();

        userRepository.save(user);

        SignUpRequest request = SignUpRequest.builder()
                .userId("testId")
                .password("testPassword")
                .name("김완수")
                .build();

        // expected
        UserIdDuplicatedException e = assertThrows(UserIdDuplicatedException.class, () -> {
            authService.signUp(request);
        });

        assertEquals("아이디가 중복됩니다.", e.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("회원가입 - 비밀번호 규칙 미준수(PasswordPolicyViolationException)")
    void 비밀번호_규칙_미준수_회원가입_테스트() {
        // given
        SignUpRequest wrongPasswordRequest = SignUpRequest.builder()
                .userId("testId")
                .password("testPassword")
                .name("김완수")
                .build();

        // expected
        PasswordPolicyViolationException e = assertThrows(PasswordPolicyViolationException.class, () -> {
            authService.signUp(wrongPasswordRequest);
        });

        assertEquals("비밀번호는 영어 대문자, 영어 소문자, 숫자, 특수문자 중 3종류 이상으로 12자리 이상의 문자열로 생성해야 합니다.", e.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("로그인 - 정상 요청")
    void 로그인_테스트() {
        // given
        User user = User.builder()
                .userId("testId")
                .password(passwordEncoder.encode("testPassword"))
                .name("김완수")
                .build();

        userRepository.save(user);

        LoginRequest loginRequest = LoginRequest.builder()
                .userId("testId")
                .password("testPassword")
                .build();

        // when
        TokenResponse response = authService.login(loginRequest);

        // then
        assertTrue(!response.accessToken().isBlank());
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 아이디(InvalidCredentialException)")
    void 없는_아이디_로그인_테스트() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .userId("testId")
                .password("testPassword")
                .build();

        // expected
        InvalidCredentialException e = assertThrows(InvalidCredentialException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("아이디가 존재하지 않거나 비밀번호가 틀렸습니다.", e.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("로그인 - 비밀번호 오류(InvalidPasswordException)")
    void 틀린_비밀번호_로그인_테스트() {
        // given
        User user = User.builder()
                .userId("testId")
                .password(passwordEncoder.encode("testPassword"))
                .name("김완수")
                .build();

        userRepository.save(user);

        LoginRequest loginRequest = LoginRequest.builder()
                .userId("testId")
                .password("wrongPassword")
                .build();

        // expected
        InvalidPasswordException e = assertThrows(InvalidPasswordException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("비밀번호가 틀렸습니다.", e.getErrorCode().getMessage());
    }
}
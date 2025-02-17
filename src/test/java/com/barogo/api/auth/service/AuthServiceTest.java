package com.barogo.api.auth.service;

import com.barogo.api.auth.exception.InvalidPasswordException;
import com.barogo.api.auth.exception.UserIdDuplicatedException;
import com.barogo.api.auth.exception.UserNotFoundException;
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
    @DisplayName("정상 회원가입 테스트")
    void 회원가입_테스트() {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .userId("testId")
                .password("testPassword")
                .name("김완수")
                .build();

        // when
        authService.signUp(request);
        User user = userRepository.findAll().get(0);

        //then
        assertEquals(1, userRepository.count());
        assertEquals("testId", user.getUserId());
        assertTrue(passwordEncoder.matches("testPassword", user.getPassword()));
        assertEquals("김완수", user.getName());
    }

    @Test
    @DisplayName("중복 아이디 회원가입 시 UserIdDuplicatedException을 반환한다")
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
    @DisplayName("정상 로그인 테스트")
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
    @DisplayName("없는 아이디로 로그인한 경우 UserNotFoundException을 반환한다.")
    void 없는_아이디_로그인_테스트() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .userId("testId")
                .password("testPassword")
                .build();

        // expected
        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("해당 사용자를 찾을 수 없습니다.", e.getErrorCode().getMessage());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인한 경우 InvalidPasswordException을 반환한다.")
    void 잘못된_비밀번호_로그인_테스트() {
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
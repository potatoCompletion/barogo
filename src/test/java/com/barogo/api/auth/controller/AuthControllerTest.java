package com.barogo.api.auth.controller;

import com.barogo.api.auth.request.LoginRequest;
import com.barogo.api.auth.request.SignUpRequest;
import com.barogo.api.user.domain.User;
import com.barogo.api.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("정상 회원가입 테스트")
    void 회원가입_테스트() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .userId("testId")
                .password("testPassword")
                .name("김완수")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원가입 시에 SignUpRequest는 빈 값일 수 없다.")
    void 빈값_회원가입_테스트() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .userId("")
                .password("")
                .name("")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST_PARAM"))
                .andExpect(jsonPath("$.message").value("요청 검증 실패했습니다."))
                .andExpect(jsonPath("$.errorList.length()").value(3))
                .andDo(print());
    }

    @Test
    @DisplayName("중복 아이디 회원가입 시 BAD_REQUEST를 반환한다")
    void 중복_아이디_회원가입_테스트() throws Exception {
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

        String jsonRequest = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.errorCode").value("USER_ID_DUPLICATED"))
                .andExpect(jsonPath("$.message").value("아이디가 중복됩니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("정상 로그인 테스트")
    void 로그인_테스트() throws Exception {
        // given
        User user = User.builder()
                .userId("testId")
                .password(passwordEncoder.encode("testPassword"))
                .name("김완수")
                .build();

        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .userId("testId")
                .password("testPassword")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("로그인 시에 LoginRequest는 빈 값일 수 없다.")
    void 빈값_로그인_테스트() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .userId("")
                .password("")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST_PARAM"))
                .andExpect(jsonPath("$.message").value("요청 검증 실패했습니다."))
                .andExpect(jsonPath("$.errorList.length()").value(2))
                .andDo(print());
    }

    @Test
    @DisplayName("요청한 로그인 정보가 DB에 없을 시 BAD_REQUEST를 반환한다.")
    void 없는유저_로그인_테스트() throws Exception {
        // given
        User user = User.builder()
                .userId("testId")
                .password(passwordEncoder.encode("testPassword"))
                .name("김완수")
                .build();

        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .userId("wrongId")
                .password("wrongPassword")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("해당 사용자를 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호가 틀렸을 시 BAD_REQUEST를 반환한다.")
    void 잘못된_비밀번호_로그인_테스트() throws Exception {
        // given
        User user = User.builder()
                .userId("testId")
                .password(passwordEncoder.encode("testPassword"))
                .name("김완수")
                .build();

        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .userId("testId")
                .password("wrongPassword")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.errorCode").value("INVALID_PASSWORD"))
                .andExpect(jsonPath("$.message").value("비밀번호가 틀렸습니다."))
                .andDo(print());
    }
}
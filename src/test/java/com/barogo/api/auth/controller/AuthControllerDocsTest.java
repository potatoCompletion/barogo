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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.barogo.api.common.util.ApiDocumentUtils.getDocumentRequest;
import static com.barogo.api.common.util.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class AuthControllerDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Spring Rest Docs - 회원가입")
    void 문서생성_회원가입() throws Exception {

        SignUpRequest request = SignUpRequest.builder()
                .userId("testuser")
                .password("password123!@#")
                .name("홍길동")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);


        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andDo(document("auth-signup",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("userId").description("사용자 ID"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("name").description("사용자 이름")
                        ),
                        responseFields(
                                fieldWithPath("message").description("회원가입 성공 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("Spring Rest Docs - 로그인")
    void 문서생성_로그인() throws Exception {
        User user = User.builder()
                .userId("testuser")
                .password(passwordEncoder.encode("password123!@#"))
                .name("김완수")
                .build();

        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .userId("testuser")
                .password("password123!@#")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);


        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andDo(document("auth-login",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("userId").description("사용자 ID"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("JWT 액세스 토큰")
                        )
                ));

    }
}
